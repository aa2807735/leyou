package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: CartService <br/>
 * Description: TODO
 * Date 2020/5/9 22:00
 *
 * @author Lenovo
 **/
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:user:id:";

    public void addCart(Cart cart) {
        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //判断是否存咋
        String key = KEY_PREFIX + user.getId();
        //hash key
        String hashKey = cart.getSkuId().toString();


        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //判断是否存在
        if (operations.hasKey(hashKey)) {
            String josn = operations.get(hashKey).toString();
            Cart parse = JsonUtils.parse(josn, Cart.class);
            parse.setNum(cart.getNum() + parse.getNum());
            operations.put(hashKey, JsonUtils.serialize(parse));
        } else {
            operations.put(hashKey, JsonUtils.serialize(cart));
        }
    }

    public List<Cart> queryCartList() {
        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //判断是否存咋
        String key = KEY_PREFIX + user.getId();

        if (!redisTemplate.hasKey(key)) {
            throw new MyException(ExceptionEnums.CART_NOT_FOUND);
        }
        //获取购物车的所有信息
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> collect = operations.values().stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
        return collect;
    }

    public void operationCartNum(Long id, Integer num) {
        //获取登陆用户
        UserInfo user = UserInterceptor.getUser();
        //判断是否存在
        String key = KEY_PREFIX + user.getId();

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);

        if (!operations.hasKey(id.toString())) {
            throw new MyException(ExceptionEnums.CART_NOT_FOUND);
        }

        String json = redisTemplate.opsForHash().get(key, String.valueOf(id)).toString();
        Cart cart = JsonUtils.parse(json, Cart.class);
        cart.setNum(num);
        operations.put(String.valueOf(id), JsonUtils.serialize(cart));
    }

    public void deleteCart(Long id) {
        //获取登陆对象
        UserInfo userInfo = UserInterceptor.getUser();

        //获取token中用户的id
        String token = KEY_PREFIX + userInfo.getId();

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(token);

        if (!operations.hasKey(id.toString())) {
            throw new MyException(ExceptionEnums.CART_NOT_FOUND);
        }
        operations.delete(id.toString());


    }
}
