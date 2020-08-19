package com.leyou.search.repository;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Spu;
import com.leyou.search.SearchService.SearchService;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    @Test
    public void testCreatIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }


    @Test
    public void loadData() {


        //查询spu
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            //查询spu的信息
            PageResult pageResult = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> items = JsonUtils.parseList(JsonUtils.serialize(pageResult.getItems()), Spu.class);
            //List<Spu> items = pageResult.getItems();    //当前页集合
            if (CollectionUtils.isEmpty(items)) {
                break;
            }
            List<Goods> collect = new ArrayList<>();
            for (Spu item : items) {
                Goods goods = searchService.buildGoods(item);
                collect.add(goods);
            }
            goodsRepository.saveAll(collect);
            page++;
            //翻页
            size = items.size();
        } while (size == 100);

    }
}
