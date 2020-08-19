package com.leyou;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName: SpikeApplication <br/>
 * Description: TODO
 * Date 2020/5/31 19:43
 *
 * @author Lenovo
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class SpikeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpikeApplication.class);
    }
}
