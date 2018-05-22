package com.tairanchina.csp.dew.example.hystrixtwo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * desription:
 * Created by ding on 2017/11/17.
 */
@SpringCloudApplication
@EnableFeignClients
public class HystrixExample2Application  {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixExample2Application.class).run(args);
    }

}
