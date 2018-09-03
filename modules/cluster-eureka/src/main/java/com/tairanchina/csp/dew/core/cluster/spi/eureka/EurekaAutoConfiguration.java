package com.tairanchina.csp.dew.core.cluster.spi.eureka;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaAutoConfiguration {

    @Value("${dew.cluster.election.config.election-period-sec:60}")
    private int electionPeriodSec;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    @ConditionalOnExpression("#{'${dew.cluster.election}'=='eureka'}")
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public EurekaClusterElection eurekaClusterElection(DiscoveryClient discoveryClient, EurekaRegistration eurekaRegistration){
        return new EurekaClusterElection(electionPeriodSec,applicationName,discoveryClient,eurekaRegistration);
    }
}
