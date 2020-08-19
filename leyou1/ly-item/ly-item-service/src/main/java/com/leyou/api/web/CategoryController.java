package com.leyou.api.web;

import com.leyou.api.service.CategoryService;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: CategoryController <br/>
 * Description: TODO
 * Date 2020/4/28 14:43
 *
 * @author Lenovo
 **/
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点查询其下的分类
     * @param pid
     * @return
     */
    @GetMapping("list")
    //ResponseEntity，这个的作用就是将tostring的字符数据转化为json的数据格式，我认为这是它的最大作用。
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid") Long pid) {
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
        //return ResponseEntity.status(HttpStatus.OK).body(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 根据商品id查询分类
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryListByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }

    @PostMapping("add")
    public ResponseEntity<Void> addCategoryNode(@RequestParam("node") Category category){
        categoryService.addCategoryNode(category);
        return ResponseEntity.noContent().build();
    }

}
