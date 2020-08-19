package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: OrderController <br/>
 * Description: TODO
 * Date 2020/5/10 13:16
 * @author Lenovo
 **/
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        //创建订单
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    @GetMapping("/secKill")
        public ResponseEntity<Void> createSecKillOrderById(@RequestParam("skuId") Long skuId, @RequestParam("userId") Long userId){
        orderService.createSecKillOrderById( skuId,  userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
