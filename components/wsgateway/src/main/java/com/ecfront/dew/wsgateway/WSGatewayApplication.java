package com.ecfront.dew.wsgateway;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
public class WSGatewayApplication {

    {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        System.setProperty("vertx.disableFileCaching", "true");
        System.setProperty("vertx.disableFileCPResolving", "true");
        System.setProperty("hazelcast.logging.type", "slf4j");
    }

    @Autowired
    private VertxServer vertxServer;

    @Bean
    @LoadBalanced
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(WSGatewayApplication.class).web(false).run(args);
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx.vertx(vertxServer.getOpt()).deployVerticle(vertxServer);
    }

}
