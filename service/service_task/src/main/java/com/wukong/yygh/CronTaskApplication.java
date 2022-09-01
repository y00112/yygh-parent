package com.wukong.yygh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
  * Created By WuKong on 2022/9/1 16:42
  **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.wukong")
@EnableScheduling // 开启定时任务
public class CronTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(CronTaskApplication.class,args);
    }
}
