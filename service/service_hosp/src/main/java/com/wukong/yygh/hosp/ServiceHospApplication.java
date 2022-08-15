package com.wukong.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * Created By WuKong on 2022/8/14 21:36
 **/
@SpringBootApplication
@MapperScan("com.wukong.yygh.hosp.mapper")
@ComponentScan(basePackages = "com.wukong")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class,args);
    }
}
