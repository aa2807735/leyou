package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: UserService <br/>
 * Description: TODO
 * Date 2020/5/7 20:14
 *
 * @author Lenovo
 **/
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:phone:";

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        //判断类型
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new MyException(ExceptionEnums.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {
        String key = KEY_PREFIX + phone ;
        String code = NumberUtils.generateCode(6);
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);

        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);  //五分钟
    }

    public void register(User user, String code) {
        //校验验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code,cacheCode)){
            throw new MyException(ExceptionEnums.INVALID_USER_REGISTER_CODE);
        }
        //对密码加密
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        String md5Hex = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(md5Hex);
        //注册
        user.setCreated(new Date());
        userMapper.insert(user);
    }

    public User queryUserByUsernameAndPassword(String username, String password) {
        User user = new User();
        user.setUsername(username);
        User record = userMapper.selectOne(user);
        //校验
        if (record==null){
            throw new MyException(ExceptionEnums.INVALID_USER_USERNAME_OR_PASSWORD);
        }
        //校验密码
        if (!StringUtils.equals(record.getPassword(), CodecUtils.md5Hex(password,record.getSalt()))) {
            throw new MyException(ExceptionEnums.INVALID_USER_USERNAME_OR_PASSWORD);
        }
        user.setId(record.getId());
        return user;
    }
}
