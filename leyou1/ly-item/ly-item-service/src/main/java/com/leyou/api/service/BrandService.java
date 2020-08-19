package com.leyou.api.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.api.mapper.BrandMapper;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.List;

/**
 * ClassName: BrandService <br/>
 * Description: TODO
 * Date 2020/4/29 7:45
 *
 * @author Lenovo
 **/
@Service
public class BrandService {


    @Autowired
    private BrandMapper brandMapper;

    /**
     * 展示所有的品牌
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        Example example = new Example(Brand.class);
        Criteria criteria = example.createCriteria();
        //分页    mybatis拦截 然后拼接limit
        PageHelper.startPage(page, rows);
        //过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.orLike("name", "%" + key + "%");
            criteria.orEqualTo("letter", key.toUpperCase());
            //过滤语句
        }
        //排序
        if (StringUtils.isNotEmpty(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new MyException(ExceptionEnums.BAND_NOT_FOUND);
        }
        PageInfo<Brand> info = new PageInfo<>(brands);


        return new PageResult<>(info.getTotal(), brands);
    }


    /**
     * 保存商品
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //保存一个实体，null的属性也会保存，不会使用数据库默认值
        int insert = brandMapper.insert(brand);
        if (insert != 1) {
            throw new MyException(ExceptionEnums.BRAND_SAVE_ERROR);
        }
        for (Long cid : cids) {
            int i = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (i != 1) {
                throw new MyException(ExceptionEnums.BRAND_SAVE_ERROR);
            }
        }
    }

    /**
     * 根据品牌id获取品牌信息
     * @param id
     * @return
     */
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new MyException(ExceptionEnums.BAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = brandMapper.queryByCategoryId(cid);
        if (CollectionUtils.isEmpty(brands)) {
            throw new MyException(ExceptionEnums.BAND_NOT_FOUND);
        }
        return brands;
    }

    public Brand queryBrandByBid(Long bid) {
        Brand brand = brandMapper.queryByBrandId(bid);
        if (brand==null){
            throw new MyException(ExceptionEnums.BAND_NOT_FOUND);
        }
        return brand;
    }

    @Transactional
    public void updateBrand(Brand brand) {
        //1.查询该品牌下的所有类别
             //List<Long> cids = brandMapper.selectCategoryByBrandId(brand.getId());
        //2.删除
        int i = brandMapper.DeleteCategoryBrand(brand.getId());
        if (i!=1){
            throw new MyException(ExceptionEnums.BRAND_UPDATE_ERROR);
        }
        //3.插入数据
        String[] cids = brand.getCids().split(",");
        for (String cid : cids) {
            int i1 = brandMapper.insertCategoryBrand(Long.valueOf(cid), brand.getId());
            if (i1!=1){
                throw new MyException(ExceptionEnums.BRAND_UPDATE_ERROR);
            }
        }
        int i2 = brandMapper.updateByPrimaryKeySelective(brand);
        if (i2!=1){
            throw new MyException(ExceptionEnums.BRAND_UPDATE_ERROR);
        }
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        return brandMapper.selectByIdList(ids);
    }
}
