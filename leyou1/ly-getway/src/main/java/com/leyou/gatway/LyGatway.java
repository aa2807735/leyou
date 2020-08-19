package com.leyou.gatway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * ClassName: LyGatway <br/>
 * Description: TODO
 * Date 2020/4/27 19:50
 *
 * @author Lenovo
 **/


@SpringCloudApplication
@EnableEurekaClient
@EnableZuulProxy
public class LyGatway {
    public static void main(String[] args) {
        SpringApplication.run(LyGatway.class);
    }
}
