package com.wukong.yygh.dict;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created By WuKong on 2022/8/17 15:22
 **/
@SpringBootApplication
@ComponentScan(basePackages = "com.wukong")
@MapperScan(basePackages = "com.wukong.yygh.dict.mapper")
@EnableDiscoveryClient // 开启nacos客户端支持
public class ServiceDictApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDictApplication.class,args);
    }
}
