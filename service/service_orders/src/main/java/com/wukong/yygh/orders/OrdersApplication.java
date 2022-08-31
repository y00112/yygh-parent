package com.wukong.yygh.orders;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created By WuKong on 2022/8/30 15:40
 **/
@SpringBootApplication
@ComponentScan(basePackages = {"com.wukong"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.wukong"})
public class OrdersApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }
}