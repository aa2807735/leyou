package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * ClassName: JwtProperties <br/>
 * Description: TODO
 * Date 2020/5/8 11:36
 *
 * @author Lenovo
 **/
@ConfigurationProperties(prefix = "leyou.jwt")
@Data
public class JwtProperties {
    private String pubKeyPath;
    private String cookieName;
    private PublicKey publicKey;

    //对象一但实例化加载,就必须读取公钥和私钥
    @PostConstruct
    public void init() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
