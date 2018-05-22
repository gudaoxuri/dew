package com.tairanchina.csp.dew.jdbc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * desription:
 * Created by ding on 2018/1/30.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).run(args);
        System.out.println("ssss");
    }
}
