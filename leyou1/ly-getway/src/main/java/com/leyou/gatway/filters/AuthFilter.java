package com.leyou.gatway.filters;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gatway.conf.FilterProperties;
import com.leyou.gatway.conf.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * ClassName: AuthFilter <br/>
 * Description: TODO
 * Date 2020/5/9 7:47
 *
 * @author Lenovo
 **/
@Component
@EnableConfigurationProperties(value = {JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {
    @Autowired
    private JwtProperties properties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取请求的url路径
        String requestURI = request.getRequestURI();
        //判断是否方向，则返回false
        return !isAllowPath(requestURI);
    }

    private boolean isAllowPath(String requestURI) {
        for (String allowPath : filterProperties.getAllowPaths()) {
            if (requestURI.startsWith(allowPath)) return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取token
        HttpServletRequest request = ctx.getRequest();
        //解析token
        String token = CookieUtils.getCookieValue(request, properties.getCookieName());
        //
        try {
            //解析token
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, properties.getPublicKey());
            //TODO 权限校验
        } catch (Exception e) {
            //解析token失败，未登录，拦截
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
        }
        return null;
    }
}
