package com.leyou.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClassName: config <br/>
 * Description: TODO
 * Date 2020/5/7 15:56
 *
 * @author Lenovo
 **/
@ConfigurationProperties(prefix = "ly.sms")
public class config {
    String key;
    String secret;
    String signName;
    String template;

}
