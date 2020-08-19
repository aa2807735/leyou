package com.leyou.search.client;

import com.leyou.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * ClassName: CategoryClient <br/>
 * Description: TODO
 * Date 2020/5/3 9:23
 * @author Lenovo
 **/
@FeignClient("item-service")
public interface CategoryClient {
    @GetMapping("category/list/ids")
    List<Category> queryCategoryListByIds(@RequestParam("ids") List<Long> ids);
}
