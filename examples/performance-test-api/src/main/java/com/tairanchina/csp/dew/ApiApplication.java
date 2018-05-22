package com.tairanchina.csp.dew;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;


/**
 * ApiApplication
 *
 * @author hzzjb
 * @date 2017/9/27
 */
@EnableFeignClients
@SpringCloudApplication
public class ApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApiApplication.class).run(args);
    }
}
