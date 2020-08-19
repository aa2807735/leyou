package com.leyou.page.serivce;

import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import com.leyou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: PageService <br/>
 * Description: TODO
 * Date 2020/5/5 9:49
 *
 * @author Lenovo
 **/
@Service
public class PageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //1.查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //2.查询sks
        List<Sku> skus = spu.getSkus();
        //3.查询详细
        SpuDetail detail = spu.getSpuDetail();
        //4.查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //5.查询分类
        List<Category> categories = categoryClient.queryCategoryListByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //6.查询规格参数
        List<Specification> specs = specificationClient.queryGroupItemByCid(spu.getCid3());

        //model.put("spu",spu);
        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("specs", specs);

        /*//特有规格参数
        model.put("specialParamName",specialParamName);
        model.put("specialParamValue",specialParamValue);
        //全部规格参数
        model.put("specName",specName);
        model.put("specValue",specValue);
        //规格参数组数据
        model.put("groups",groups);*/
        return model;
    }

    public void createHtml(Long spuId) {
        try {
            //上下文
            Context context = new Context();
            context.setVariables(loadModel(spuId));
            //
            File dest = new File("D:\\nginx-1.4.4\\html\\leyou\\" + spuId + ".html");
            if (dest.exists()) {
                dest.delete();
            }
            PrintWriter writer = new PrintWriter(dest, "UTF-8");
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteHtml(Long spuId) {
        File dest = new File("D:\\nginx-1.4.4\\html\\leyou\\" + spuId + ".html");
        if (dest.exists()) {
            dest.delete();
        }
    }
}
