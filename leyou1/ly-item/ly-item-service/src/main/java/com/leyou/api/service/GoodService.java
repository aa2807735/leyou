package com.leyou.api.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.api.annotation.AdminOnly;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.vo.PageResult;
import com.leyou.api.mapper.SkuMapper;
import com.leyou.api.mapper.SpuDetailMapper;
import com.leyou.api.mapper.SpuMapper;
import com.leyou.api.mapper.StockMapper;
import com.leyou.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: GoodService <br/>
 * Description: TODO
 * Date 2020/4/30 17:25
 *
 * @author Lenovo
 **/
@Service
public class GoodService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 显示spu分页结果
     *
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @AdminOnly
    public PageResult querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }
        example.setOrderByClause("last_update_time DESC");
        //默认排序

        List<Spu> spus = spuMapper.selectByExample(example);
        loadCategoryAndBrandName(spus);


        //解析分类和品牌的名称


        if (CollectionUtils.isEmpty(spus)) {
            throw new MyException(ExceptionEnums.Goods_SPU_NOT_FOUND);
        }
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
        return new PageResult<>(spuPageInfo.getTotal(), spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            String categoryName = "";
            //获取分类名称
            List<Category> list = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            for (Category category : list) {
                categoryName = categoryName + category.getName() + "/";
            }
            spu.setCname(categoryName);
            //处理品牌名称
            Long brandId = spu.getBrandId();
            Brand brand = brandService.queryById(brandId);
            spu.setBname(brand.getName());
        }
    }


    /**
     * 添加商品
     *
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int insert = spuMapper.insert(spu);

        if (insert != 1) {
            throw new MyException(ExceptionEnums.Goods_SAVE_ERROR);
        }
        //新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);
        //新增sku和stock
        saveSkuAndStock(spu);

        //发送mq消息
        amqpTemplate.convertAndSend("item.insert", spu.getId());
    }

    /**
     * 新增sku和stock的抽取方法
     *
     * @param spu
     */
    private void saveSkuAndStock(Spu spu) {
        //新增sku
        List<Sku> skus = spu.getSkus();
        List<Stock> stockList = new ArrayList<>();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int count = skuMapper.insert(sku);
            if (count != 1) {
                throw new MyException(ExceptionEnums.Goods_SAVE_ERROR);
            }
            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        stockMapper.insertList(stockList);
    }

    /**
     * 根据spuId 获取商品详情
     *
     * @param supId
     * @return
     */
    public SpuDetail queryDetailById(Long supId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(supId);
        if (spuDetail == null) {
            throw new MyException(ExceptionEnums.GOOD_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkyBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> list = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(list)) {
            throw new MyException(ExceptionEnums.GOOD_SKU_NOT_FOUND);
        }
        //查询库存

        queryStock(list);

        return list;
    }

    private void queryStock(List<Sku> list) {
        for (Sku sku1 : list) {
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
            if (stock == null) {
                throw new MyException(ExceptionEnums.SPEC_PARAM_NOT_FOUND);
            }
            sku1.setStock(stock.getStock());
        }
    }

    /**
     * 修改商品信息
     *
     * @param spu
     */
    public void updateGoods(Spu spu) {
        if (spu.getId() == null) {
            throw new MyException(ExceptionEnums.GOODS_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> select = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(select)) {
            //删除sku和stock
            skuMapper.delete(sku);
            List<Long> ids = select.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(null);
        spu.setCreateTime(null);


        int i = spuMapper.updateByPrimaryKeySelective(spu);
        if (i != 1) {
            throw new MyException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }
        //修改detail
        int i1 = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (i1 != 1) {
            throw new MyException(ExceptionEnums.GOODS_UPDATE_ERROR);
        }

        //新增sku和detail
        saveSkuAndStock(spu);

        //发送mq消息
        amqpTemplate.convertAndSend("item.update", spu.getId());
    }

    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new MyException(ExceptionEnums.Goods_SPU_NOT_FOUND);
        }
        //查询sku
        List<Sku> skus = this.querySkyBySpuId(id);
        spu.setSkus(skus);
        //查询detail
        SpuDetail spuDetail = this.queryDetailById(id);
        spu.setSpuDetail(spuDetail);
        return spu;
    }

    public List<Sku> querySkyBySpuIds(List<Long> spuId) {
        List<Sku> skus = skuMapper.selectByIdList(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            throw new MyException(ExceptionEnums.GOOD_SKU_NOT_FOUND);
        }
        queryStock(skus);
        return skus;
    }

    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOS) {
        for (CartDTO cartDTO : cartDTOS) {
            int count = stockMapper.decreaseStock(cartDTO.getSkuId(), cartDTO.getNum());
            if (count != 1) {
                throw new MyException(ExceptionEnums.CART_NOT_FOUND);
            }
        }
    }

    public void updateStock(Long skuId) {
        Stock stock = stockMapper.selectByPrimaryKey(skuId);
        Long version = stock.getVersion();  //查询版本
        int result = stockMapper.updateStock(skuId,version);//修改库存


        if (result!=1){  //没有库存了
            return;
            //throw new MyException(ExceptionEnums.STOCK_NOT_ENOUGH);
        }
    }
}
