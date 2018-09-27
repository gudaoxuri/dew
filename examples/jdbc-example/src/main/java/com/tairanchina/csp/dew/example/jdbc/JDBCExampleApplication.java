package com.tairanchina.csp.dew.example.jdbc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@SpringBootApplication
public class JDBCExampleApplication {

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(JDBCExampleApplication.class).run(args);
    }

}
