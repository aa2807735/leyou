package com.leyou.api;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: GoodApi <br/>
 * Description: TODO
 * Date 2020/5/3 10:14
 *
 * @author Lenovo
 **/
public interface GoodApi {

    @GetMapping("/spu/page")
    PageResult querySpuByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                              @RequestParam(value = "saleable", required = false) Boolean saleable,
                              @RequestParam(value = "key", required = false) String key);

    /**
     * 根据spu的id查询详情
     *
     * @param supId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id") Long supId);

    /**
     * 根据spu查询下面的所有sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    List<Sku> querySkyBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据spu的id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据id批量查询
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list/ids")
    List<Sku> querySkyBySpuIds(@RequestParam("ids") List<Long> spuId);

    /**
     * 修改库存
     *
     * @param cartDTOS
     * @return
     */
    @PostMapping("stock/decrease")
    Void decreaseStock(@RequestBody List<CartDTO> cartDTOS);

    /**
     * 添加秒杀，数量减一
     * @param skuId 商品id
     * @return
     */
    @PostMapping("stock/updateStock")
    ResponseEntity<Void> updateStock(@RequestParam("skuId") Long skuId);

}
