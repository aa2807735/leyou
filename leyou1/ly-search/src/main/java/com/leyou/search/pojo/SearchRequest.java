package com.leyou.search.pojo;

import lombok.Data;

import java.util.Map;

/**
 * ClassName: SearchRequest <br/>
 * Description: TODO
 * Date 2020/5/3 15:55
 *
 * @author Lenovo
 **/
@Data
public class SearchRequest {
    private String key;
    private Integer page;
    private Map<String,String> filter;

    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
