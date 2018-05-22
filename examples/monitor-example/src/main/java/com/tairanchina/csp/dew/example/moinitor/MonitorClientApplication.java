package com.tairanchina.csp.dew.example.moinitor;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 工程启动类
 */
@SpringCloudApplication
public class MonitorClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MonitorClientApplication.class).run(args);
    }

}
