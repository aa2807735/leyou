package com.leyou.api.web;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.api.service.GoodService;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: GoodsController <br/>
 * Description: TODO
 * Date 2020/4/30 17:26
 *
 * @author Lenovo
 **/
@RestController
public class GoodsController {

    @Autowired
    private GoodService goodService;


    @GetMapping("/spu/page")
    public ResponseEntity<PageResult> querySpuByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                     @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                     @RequestParam(value = "saleable", required = false) Boolean saleable,
                                                     @RequestParam(value = "key", required = false) String key) {
        return ResponseEntity.ok(goodService.querySpuByPage(page, rows, saleable, key));
    }

    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品的数据
     *
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
        goodService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 根据spu的id查询详情
     *
     * @param supId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long supId) {
        return ResponseEntity.ok(goodService.queryDetailById(supId));
    }

    /**
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkyBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodService.querySkyBySpuId(spuId));
    }

    /**
     * 根据sku查询集合
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list/ids")
    public ResponseEntity<List<Sku>> querySkyBySpuIds(@RequestParam("ids") List<Long> spuId) {
        return ResponseEntity.ok(goodService.querySkyBySpuIds(spuId));
    }


    /**
     * 根据spu的id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodService.querySpuById(id));
    }


    /**
     * 减库存的操作
     * @param cartDTOS
     * @return
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> cartDTOS) {
        goodService.decreaseStock(cartDTOS);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("stock/updateStock")
    public ResponseEntity<Void> updateStock(@RequestParam("skuId") Long skuId) {
        goodService.updateStock(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
