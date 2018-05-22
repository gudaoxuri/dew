package com.tairanchina.csp.dew;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;


/**
 * ServiceApplication
 *
 * @author hzzjb
 * @date 2017/9/19
 */
@SpringCloudApplication
public class ServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceApplication.class).run(args);
    }
}
