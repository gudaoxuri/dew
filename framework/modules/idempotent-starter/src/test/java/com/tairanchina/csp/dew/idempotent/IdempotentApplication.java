package com.tairanchina.csp.dew.idempotent;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class IdempotentApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(IdempotentApplication.class).run(args);
    }
}
