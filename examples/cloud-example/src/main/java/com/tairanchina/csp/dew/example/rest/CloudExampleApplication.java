package com.tairanchina.csp.dew.example.rest;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class CloudExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CloudExampleApplication.class).run(args);
    }

}
