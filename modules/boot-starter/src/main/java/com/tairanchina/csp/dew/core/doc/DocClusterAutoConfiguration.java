package com.tairanchina.csp.dew.core.doc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(DiscoveryClient.class)
public class DocClusterAutoConfiguration {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public DocController docController() {
        return new DocController(() -> discoveryClient.getServices().stream().map(serviceId -> {
            ServiceInstance instance = discoveryClient.getInstances(serviceId).get(0);
            return (instance.isSecure() ? "https" : "http") + "://" + instance.getHost() + ":" + instance.getPort() + instance.getUri() + "/v2/api-docs";
        }).collect(Collectors.toList()));
    }

}
