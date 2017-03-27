package com.ecfront.dew.logger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

@EnableZipkinStreamServer
@EnableDiscoveryClient
@SpringBootApplication
public class LoggerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LoggerApplication.class).web(true).run(args);
    }

}
