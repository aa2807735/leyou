package com.leyou.search.pojo.search.client;


import com.leyou.pojo.Category;
import com.leyou.search.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void queryCategoryListByIds() {
        List<Category> list = categoryClient.queryCategoryListByIds(Arrays.asList(1L, 2L, 3L));
        for (Category category : list) {
            System.out.println("category = " + category);
        }
    }

}