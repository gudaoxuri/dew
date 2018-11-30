package com.trc.dew;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JdbcApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(JdbcApplication.class).web(true).run(args);
    }
}
