package com.tairanchina.csp.dew.core.cluster.spi.eureka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='eureka' || '${dew.cluster.mq}'=='eureka' || '${dew.cluster.lock}'=='eureka' || '${dew.cluster.map}'=='eureka' || '${dew.cluster.election}'=='eureka'}")
public class EurekaAutoConfiguration {

    @Value("${dew.cluster.config.instance-period-sec:60}")
    private int electionPeriodSec;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public EurekaClusterElectionWrap eurekaClusterElection(DiscoveryClient discoveryClient, EurekaRegistration eurekaRegistration) {
        return new EurekaClusterElectionWrap(electionPeriodSec, applicationName, discoveryClient, eurekaRegistration);
    }

}
