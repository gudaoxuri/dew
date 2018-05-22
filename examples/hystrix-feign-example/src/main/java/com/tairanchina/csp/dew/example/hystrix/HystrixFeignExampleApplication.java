package com.tairanchina.csp.dew.example.hystrix;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 工程启动类
 */
@SpringCloudApplication
@EnableFeignClients
public class HystrixFeignExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixFeignExampleApplication.class).run(args);
    }

}
