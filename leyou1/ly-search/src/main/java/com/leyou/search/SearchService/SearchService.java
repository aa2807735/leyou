package com.leyou.search.SearchService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * ClassName: SearchService <br/>
 * Description: TODO
 * Date 2020/5/3 10:59
 *
 * @author Lenovo
 **/
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;


    public Goods buildGoods(Spu spu) {

        Long spuId = spu.getId();

        //1.搜索字段all
        //查询分类
        List<Category> list = categoryClient.queryCategoryListByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(list)) {
            throw new MyException(ExceptionEnums.CATEGORY_NOT_FOUND);
        }
        List<String> collect = list.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new MyException(ExceptionEnums.BAND_NOT_FOUND);
        }

        //查询sku
        List<Sku> skuList = goodsClient.querySkyBySpuId(spuId);
        if (CollectionUtils.isEmpty(skuList)) {
            throw new MyException(ExceptionEnums.GOOD_SKU_NOT_FOUND);
        }
        //对sku进行处理
        List<Map<String, Object>> skus = new ArrayList<>();
        Set<Long> priceSet = new HashSet<>();

        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("price", sku.getPrice());
            map.put("title", sku.getTitle());
            map.put("images", StringUtils.substringBefore(sku.getImages(), ","));
            //防止空指针 sku.getImages().split(",")[0];
            skus.add(map);
            priceSet.add(sku.getPrice());
        }

        String all = spu.getTitle() + StringUtils.join(collect, " ") + brand.getName();

        //查询规格参数   查询所有的
        List<SpecParam> specParams = specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(specParams)) {
            throw new MyException(ExceptionEnums.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);

        //获取通用规格参数  只有值 没有键
        String genericSpec = spuDetail.getGenericSpec();
        Map<String, String> stringStringMap = JsonUtils.parseMap(genericSpec, String.class, String.class);

        //获取特有规格参数  只有值 没有键
        String specialSpec = spuDetail.getSpecialSpec();
        Map<String, List<Object>> stringListMap = JsonUtils
                .nativeRead(specialSpec, new TypeReference<Map<String, List<Object>>>() {
                });

        //规格参数,key是规格参数的名字 ，值是规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam specParam : specParams) {
            //规格名称
            String key = specParam.getName();
            Object value = "";
            //判断石否是通用参数
            if (specParam.getGeneric()) {    //如果是
                value = stringStringMap.get(specParam.getId().toString());
                //判断是否是数值类型
                if (specParam.getNumeric()) {
                    //处理成段
                    value = chooseSegment((String) value, specParam);
                }
            } else {
                value = stringListMap.get(specParam.getId().toString());
            }
            specs.put(key, value);
        }

        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spuId);
        goods.setAll(all);   // 搜索字段，包含标题，分类，品牌，规格等
        goods.setPrice(priceSet);// 所有sku的价格集合
        goods.setSkus(JsonUtils.serialize(skus));//  所有sku的集合的json格式
        goods.setSpecs(specs);// 所有课搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }


    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest searchRequest) {
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();


        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));
        //过滤
        QueryBuilder baseQuery = buildBasicQuery(searchRequest);

        queryBuilder.withQuery(baseQuery);

        //聚合
        //1.聚合分类
        String categoryAggName = "category_name";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //2.聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询
        AggregatedPage<Goods> resultForPage = template.queryForPage(queryBuilder.build(), Goods.class);

        //字段过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, new String[]{}));
        //查询
        Page<Goods> search = goodsRepository.search(queryBuilder.build());

        //解析结果
        long totalElements = search.getTotalElements();
        Long totalPage = Long.valueOf(search.getTotalPages());
        List<Goods> content = search.getContent();

        Aggregations aggregations = resultForPage.getAggregations();
        List<Category> categories = parseCategoryAgg(aggregations.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));


        List<Map<String, Object>> specs = new ArrayList<>();


        if (categories!=null && categories.size()==1){  //一个类别
                specs = buildSpecificationAgg(categories.get(0).getId(),baseQuery);
        }

        return new SearchResult(totalElements, totalPage, content,categories,brands,specs);
    }

    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));
        //过滤条件
        Map<String, String> filter = searchRequest.getFilter();
        for (Map.Entry<String, String> stringStringEntry : filter.entrySet()) {
            String key = stringStringEntry.getKey();
            if ("cid3".equals(key) || "brandId".equals(key)){
                key = key;
            }else{
                key = "specs." + key + ".keyword";
            }
            String value = stringStringEntry.getValue();
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,value));
        }
        return boolQueryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long id, QueryBuilder baseQuery) {
        List<Map<String, Object>> result = new ArrayList<>();
        //1.查询需要进行聚合的规格参数
        List<SpecParam> specParams = specificationClient.queryParamList(null, id, true);
        //2.聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(baseQuery);
        //3.解析
        for (SpecParam param : specParams) {
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + param.getName() + ".keyword"));
        }
        AggregatedPage<Goods> goods = template.queryForPage(queryBuilder.build(), Goods.class);
        Aggregations aggregations = goods.getAggregations();
        for (SpecParam specParam : specParams) {
            StringTerms aggregation = aggregations.get(specParam.getName());
            List<String> collect = aggregation.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String,Object> map = new HashMap<>();
            map.put("k",specParam.getName());
            map.put("options",collect);
            result.add(map);
        }
        return result;
    }

    private List<Brand> parseBrandAgg(LongTerms aggregation) {
        try {
            List<Long> collect = aggregation.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(collect);
            return brands;
        } catch (Exception e) {
            return null;
        }

    }

    private List<Category> parseCategoryAgg(LongTerms aggregation) {

        try {
            List<Long> collect = aggregation.getBuckets().stream().
                    map(b -> b.getKeyAsNumber().longValue()).
                    collect(Collectors.toList());
            List<Category> categories = null;
            categories = categoryClient.queryCategoryListByIds(collect);
            return categories;
        } catch (Exception e) {
            return null;
        }

    }

    public void creatrOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        Goods goods = buildGoods(spu);
        //存入索引库
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
