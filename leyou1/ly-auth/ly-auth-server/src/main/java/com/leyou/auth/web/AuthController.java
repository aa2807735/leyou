package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.MyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.CookieUtilsOther;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: AuthController <br/>
 * Description: TODO
 * Date 2020/5/8 12:14
 *
 * @author Lenovo
 **/
@RestController
@EnableConfigurationProperties(value = {JwtProperties.class})
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;

    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpServletResponse response, HttpServletRequest request) {
        String token = authService.login(username, password);
        Cookie ly_token = new Cookie("LY_TOKEN", token);
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        // 将token写入cookie --- 工厂模式
        // httpOnly()：避免别的js代码来操作你的cookie，是一种安全措施
        // charset(): 不需要编码 因为token中没有中文
        // maxAge()： cookie的生命周期，默认是-1，代表一次会话，浏览器关闭cookie就失效
        // response: 将cookie写入 --- response中有一个方法 addCookie()
        // request: cookie中有域的概念 domain 例如一个cookie只能在www.baidu.com生效，无法在别的域下生效
        // 给cookie绑定一个域，防止别的网站访问你的cookie，也是一种安全措施
        CookieUtilsOther.newBuilder(response).httpOnly().request(request).build(prop.getCookieName(), token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登陆状态
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token, HttpServletResponse response, HttpServletRequest request) {
        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //重新生成token
            String s = JwtUtils.generateToken(infoFromToken, prop.getPrivateKey(), prop.getExpire());
            CookieUtilsOther.newBuilder(response).httpOnly().request(request).build(prop.getCookieName(), token);
            return ResponseEntity.ok(infoFromToken);
        } catch (Exception e) {
            //token 以及过期  token无效
            throw new MyException(ExceptionEnums.UN_AUTHORIZED);
        }
    }
}
