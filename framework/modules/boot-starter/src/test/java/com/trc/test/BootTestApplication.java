package com.trc.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BootTestApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BootTestApplication.class).run(args);
    }

}