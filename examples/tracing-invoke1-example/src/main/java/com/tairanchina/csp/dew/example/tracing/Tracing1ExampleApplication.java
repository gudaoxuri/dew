package com.tairanchina.csp.dew.example.tracing;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class Tracing1ExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Tracing1ExampleApplication.class).run(args);
    }

}
