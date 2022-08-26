package com.wukong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created By WuKong on 2022/8/25 9:40
 * Process finished with exit code 1
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGateWayApplication.class,args);
    }
}
