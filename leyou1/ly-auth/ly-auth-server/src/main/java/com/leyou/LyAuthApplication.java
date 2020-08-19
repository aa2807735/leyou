package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName: LyAuthApplication <br/>
 * Description: TODO
 * Date 2020/5/8 10:56
 * @author Lenovo
 **/
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class LyAuthApplication {
    public static void main(String[] args){
        SpringApplication.run(LyAuthApplication.class);
    }
}
