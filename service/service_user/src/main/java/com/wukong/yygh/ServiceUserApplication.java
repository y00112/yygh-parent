package com.wukong.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created By WuKong on 2022/8/26 22:25
 **/
@SpringBootApplication
@ComponentScan(basePackages = "com.wukong")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wukong")
@MapperScan("com.wukong.yygh.user.mapper")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}