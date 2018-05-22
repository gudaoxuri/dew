package com.tairanchina.csp.dew.core.cluster.spi.ignite;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desription:
 * Created by ding on 2018/1/25.
 */
@Configuration
@EnableConfigurationProperties(IgniteConfig.class)
public class IgniteAutoConfiguration {

    private IgniteConfig igniteConfig;

    public IgniteAutoConfiguration(IgniteConfig igniteConfig) {
        this.igniteConfig = igniteConfig;
    }

    @Bean
    @ConditionalOnExpression("#{'${dew.cluster.cache}'=='ignite' || '${dew.cluster.mq}'=='ignite' || '${dew.cluster.dist}'=='ignite'}")
    public IgniteAdapter igniteAdapter(){
        return new IgniteAdapter(igniteConfig);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.dist}'=='ignite'")
    public IgniteClusterDist igniteClusterDist(IgniteAdapter igniteAdapter){
        return new IgniteClusterDist(igniteAdapter);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='ignite'")
    public IgniteClusterMQ igniteClusterMQ(IgniteAdapter igniteAdapter){
        return new IgniteClusterMQ(igniteAdapter);
    }

}
