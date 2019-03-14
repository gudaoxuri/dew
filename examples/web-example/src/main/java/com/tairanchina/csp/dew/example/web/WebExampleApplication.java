package com.tairanchina.csp.dew.example.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 *
 * visit : http://127.0.0.1/doc.html
 */
@SpringBootApplication
public class WebExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WebExampleApplication.class).run(args);
    }

}
