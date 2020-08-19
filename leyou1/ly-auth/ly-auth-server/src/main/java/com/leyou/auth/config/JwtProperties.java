package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
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
    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private Integer expire;
    private String cookieName;
    
    private PublicKey publicKey;
    private PrivateKey privateKey;

    //对象一但实例化加载,就必须读取公钥和私钥
    @PostConstruct
    public void init() throws Exception {
        // 公钥私钥 如果不存在
        File pubPath = new File(pubKeyPath);
        File priPath = new File(priKeyPath);
        if (!pubPath.exists() || !priPath.exists()){
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }

        // 读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}
