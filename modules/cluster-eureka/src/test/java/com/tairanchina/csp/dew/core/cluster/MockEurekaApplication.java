package com.tairanchina.csp.dew.core.cluster;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MockEurekaApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MockEurekaApplication.class).web(true).run(args);
    }

}
