package com.tairanchina.csp.dew.example.config;

import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringCloudApplication
public class ConfigExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigExampleApplication.class).web(true).run(args);
    }

}
