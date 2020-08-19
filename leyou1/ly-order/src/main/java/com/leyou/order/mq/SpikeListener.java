package com.leyou.order.mq;

import com.leyou.order.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: OrderListener <br/>
 * Description: TODO
 * Date 2020/6/1 10:59
 *
 * @author Lenovo
 **/

@Component
public class SpikeListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.spike.insert.queue", durable = "true"),
            exchange = @Exchange(name = "leyou.spike.exchange", type = ExchangeTypes.TOPIC),
            key = {"spikeOrder.insert"}
    ))
    public void insertOrder(String s) {
        if (StringUtils.isEmpty(s)) {
            return;
        }
        String[] split = s.split("#");

        orderService.createSecKillOrderById(Long.valueOf(split[1]), Long.valueOf(split[0]));

    }


}
