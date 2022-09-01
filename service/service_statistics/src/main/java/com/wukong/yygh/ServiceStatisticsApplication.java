package com.wukong.yygh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created By WuKong on 2022/9/1 19:18
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.wukong"})
@ComponentScan(basePackages = {"com.wukong"})
public class ServiceStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class, args);
    }

}