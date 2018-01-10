package com.ecfront.dew.example.hystrixtwo;

import com.ecfront.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * desription:
 * Created by ding on 2017/11/17.
 */
@EnableFeignClients
public class HystrixExample2Application extends DewCloudApplication {

    public static void main(String[] args) {
        System.out.println("started");
        new SpringApplicationBuilder(HystrixExample2Application.class).run(args);
    }
}
