package com.leyou.spike.web;

import com.leyou.spike.service.SpikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: SpikeController <br/>
 * Description: TODO
 * Date 2020/5/31 20:00
 *
 * @author Lenovo
 **/
@RestController
public class SpikeController {
    @Autowired
    private SpikeService spikeService;

    /**
     * 添加秒杀skuId
     *
     * @param skuId  商品skuId
     * @param userId 用户Id
     * @return 无
     */
    @RequestMapping("spikeGoods")
    public ResponseEntity<Void> spikeGoods(@RequestParam("skuId") Long skuId, @RequestParam("userId") Long userId) {
        spikeService.spikeGoods(skuId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @RequestMapping("addSpikeTokenBucket")
    public ResponseEntity<Void> addTokenBucket(@RequestParam("skuId") Long skuId, @RequestParam("num") Long num) {
        spikeService.addTokenBucket(skuId, num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
