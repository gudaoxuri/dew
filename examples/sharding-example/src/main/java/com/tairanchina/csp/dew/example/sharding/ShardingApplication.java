package com.tairanchina.csp.dew.example.sharding;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ShardingApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ShardingApplication.class).run(args);
    }
}
