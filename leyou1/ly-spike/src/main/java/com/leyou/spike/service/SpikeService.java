package com.leyou.spike.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * ClassName: SpikeService <br/>
 * Description: TODO
 * Date 2020/5/31 20:07
 * @author Lenovo
 **/
@Service
@Slf4j
public class SpikeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private AmqpTemplate amqpTemplate;

    public void spikeGoods(Long skuId, Long userId) {
        //1.判断用户频率
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(userId + "", skuId + "");//对应setnx
        if (aBoolean != null) {
            stringRedisTemplate.expire(userId + "", 10, TimeUnit.SECONDS);
            log.info("用户频率判断，已经抢购过,商品编号：{}，用户编号：{}", skuId, userId);
            throw new MyException(ExceptionEnums.SECKILL_FAST_ERROR);
        }
        //2.利用redis生成token桶 减少数据库压力，从桶里面获取数据
        String token = stringRedisTemplate.opsForList().leftPop(skuId + "");
        if (StringUtils.isEmpty(token)) {    //如果没了
            log.info("商品：{}已经售窑了，请下次再来噢！", skuId);
            //return;
            throw new MyException(ExceptionEnums.STOCK_NOT_ENOUGH); //库存不足
        }
        //3.调用商品服务中的减少库存
        amqpTemplate.convertAndSend("spikeStock.decrease", skuId);
        //4.发送mq消息 生成订单
        amqpTemplate.convertAndSend("spikeOrder.insert", userId + "#" + skuId);

//        //3.调用商品服务中的减少库存
//        goodClient.updateStock(skuId);
//        //4.添加订单
//        orderClient.createSecKillOrderById(skuId, userId);
    }


    /**
     * 生成token桶
     *
     * @param num 需要秒杀的数量
     */
    public List<String> createTokenBucket(Long num) {
        List<String> tokenList = new ArrayList<String>();
        for (int i = 0; i < num; i++) {
            tokenList.add("secKillToken_" + UUID.randomUUID().toString().replace("-", ""));
        }
        return tokenList;
    }

    public void addTokenBucket(Long skuId, Long num) {
        List<String> tokenBucket = createTokenBucket(num);//产生一百个token桶
        stringRedisTemplate.opsForList().leftPushAll(skuId + "", tokenBucket);  //添加队列
    }
}
