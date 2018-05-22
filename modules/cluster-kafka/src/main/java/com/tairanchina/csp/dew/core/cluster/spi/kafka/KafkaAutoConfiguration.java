package com.tairanchina.csp.dew.core.cluster.spi.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * desription:
 * Created by ding on 2018/1/25.
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {

    private KafkaProperties kafkaProperties;

    public KafkaAutoConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='kafka'")
    public KafkaAdapter kafkaAdapter(){
        return new KafkaAdapter(kafkaProperties);
    }

    @Bean
    @ConditionalOnBean(KafkaAdapter.class)
    public KafkaClusterMQ kafkaClusterMQ(KafkaAdapter kafkaAdapter){
        return new KafkaClusterMQ(kafkaAdapter);
    }
}
