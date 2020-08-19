package com.leyou.order.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.order.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: UserInterceptor <br/>
 * Description: TODO
 * Date 2020/5/9 21:13
 * @author Lenovo
 **/
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties jwtProperties;

    // 定义一个线程域，存放登录用户
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            System.out.println("cookie.getValue() = " + cookie.getValue());
//        }
//        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
//
//        try {
//            //解析token
//            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
//            //传递user
//            tl.set(infoFromToken);
//            //放行
//            return true;
//        } catch (Exception e) {
//            log.error("[订单中心] 用户未授权{}", token,e);
//            e.printStackTrace();
//            return false;
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //最后用完数据 清空
        tl.remove();
    }

    public static UserInfo getUser() {
        return tl.get();
    }
}
