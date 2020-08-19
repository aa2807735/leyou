package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * ClassName: AuthService <br/>
 * Description: TODO
 * Date 2020/5/8 12:15
 *
 * @author Lenovo
 **/
@Service
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties properties;

    public String login(String username, String password) {
        User user = userClient.queryUserByUsernameAndPassword(username, password);
        if (user == null) {
            throw new MyException(ExceptionEnums.INVALID_USER_USERNAME_OR_PASSWORD);
        }
        //生成token
        try {
            return JwtUtils.generateToken(new UserInfo(user.getId(), username), properties.getPrivateKey(), properties.getExpire());
        } catch (Exception e) {
            log.error("[授权中心] 用户名或者密码错误，用户名称:{}", username, e);
            throw new MyException(ExceptionEnums.INVALID_USER_USERNAME_OR_PASSWORD);
        }
    }
}
