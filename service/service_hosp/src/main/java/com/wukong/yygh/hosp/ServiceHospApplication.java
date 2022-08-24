package com.wukong.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * Created By WuKong on 2022/8/14 21:36
 **/
@SpringBootApplication
@MapperScan("com.wukong.yygh.hosp.mapper")
@ComponentScan(basePackages = "com.wukong")
@EnableDiscoveryClient // 开启nacos客户端支持
@EnableFeignClients(basePackages = "com.wukong") // 不仅仅扫描当前模块，连当前模块所以依赖的jar包中也扫描
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class,args);
    }
}
