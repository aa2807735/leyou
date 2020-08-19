package com.leyou.order.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ClassName: OrderApi <br/>
 * Description: TODO
 * Date 2020/5/31 21:08
 *
 * @author Lenovo
 **/
public interface OrderApi {
    @GetMapping("order/secKill")
    ResponseEntity<Void> createSecKillOrderById(@RequestParam("skuId") Long skuId, @RequestParam("userId") Long userId);
}
