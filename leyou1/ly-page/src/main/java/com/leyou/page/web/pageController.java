package com.leyou.page.web;

import com.leyou.page.serivce.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;



/**
 * ClassName: pageController <br/>
 * Description: TODO
 * Date 2020/5/4 22:34
 * @author Lenovo
 **/
@Controller
public class pageController {

    @Autowired
    private PageService pageService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId,Model model){
        //查询模型数据
        Map<String,Object> attributes = pageService.loadModel(spuId);
        //准备模型数据
        model.addAllAttributes(attributes);
        //返回视图
        pageService.createHtml(spuId);
        
        return "item";
    }
}
