package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * ClassName: LyItemApplication <br/>
 * Description: TODO
 * Date 2020/4/27 20:41
 *
 * @author Lenovo
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.leyou.api.mapper")
public class LyItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyItemApplication.class);
    }
}
