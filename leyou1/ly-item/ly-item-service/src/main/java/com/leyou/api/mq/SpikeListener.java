package com.leyou.api.mq;

import com.leyou.api.service.GoodService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Pipe;

/**
 * ClassName: SpikeListener <br/>
 * Description: TODO
 * Date 2020/6/1 11:18
 *
 * @author Lenovo
 **/
@Component
public class SpikeListener {

    @Autowired
    private GoodService goodService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.spike.decrease.queue", durable = "true"),
            exchange = @Exchange(name = "leyou.spike.exchange", type = ExchangeTypes.TOPIC),
            key = {"spikeStock.decrease"})
    )
    public void decreaseStock(Long skuId) {
        if (skuId == null) {
            return;
        }
        goodService.updateStock(skuId);
    }
}
