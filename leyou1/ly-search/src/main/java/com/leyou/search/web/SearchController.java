package com.leyou.search.web;

import com.leyou.common.vo.PageResult;
import com.leyou.search.SearchService.SearchService;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: SearchController <br/>
 * Description: TODO
 * Date 2020/5/3 16:01
 *
 * @author Lenovo
 **/
@RestController

public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 搜索功能
     * @param searchRequest
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.search(searchRequest));
    }

}
