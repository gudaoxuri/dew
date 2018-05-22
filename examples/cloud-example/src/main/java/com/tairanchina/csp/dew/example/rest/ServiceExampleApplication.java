package com.tairanchina.csp.dew.example.rest;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class ServiceExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceExampleApplication.class).run(args);
    }

}
