package com.leyou.sms.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: SmsUtils <br/>
 * Description: TODO
 * Date 2020/5/7 16:10
 *
 * @author Lenovo
 **/
@Slf4j
@Component
public class SmsUtils {
    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX = "user:verify:phone:";
    private static final Long SMS_MIN_INTERVAL_IN_TIME = 60000L;
    public void sendMsm(String Phone, String Code) {
        String LastTime = template.opsForValue().get(KEY_PREFIX + Phone);
        if (!StringUtils.isBlank(LastTime)) {
            Long last = Long.valueOf(LastTime);
            if (System.currentTimeMillis() - last < SMS_MIN_INTERVAL_IN_TIME) {
                return;
            }
        }
        System.out.println("已经为" + Phone + "发送了短信验证码：" + Code);
        log.info("已经发送了短信");

        template.opsForValue().set(KEY_PREFIX + Phone, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.MINUTES);
    }
}
