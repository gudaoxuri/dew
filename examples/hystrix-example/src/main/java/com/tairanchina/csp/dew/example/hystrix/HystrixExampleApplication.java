package com.tairanchina.csp.dew.example.hystrix;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 工程启动类
 */
@SpringCloudApplication
public class HystrixExampleApplication  {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixExampleApplication.class).run(args);
    }

}
