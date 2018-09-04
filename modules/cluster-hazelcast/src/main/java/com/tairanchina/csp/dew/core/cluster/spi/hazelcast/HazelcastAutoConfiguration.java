package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HazelcastInstance.class)
@EnableConfigurationProperties(HazelcastConfig.class)
public class HazelcastAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastAutoConfiguration.class);

    private HazelcastConfig hazelcastConfig;

    public HazelcastAutoConfiguration(HazelcastConfig hazelcastConfig) {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        this.hazelcastConfig = hazelcastConfig;
    }

    @Bean
    @ConditionalOnExpression("#{'${dew.cluster.cache}'=='hazelcast' || '${dew.cluster.mq}'=='hazelcast' || '${dew.cluster.lock}'=='hazelcast' || '${dew.cluster.map}'=='hazelcast'}")
    public HazelcastAdapter hazelcastAdapter() {
        return new HazelcastAdapter(hazelcastConfig);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.lock}'=='hazelcast'")
    public HazelcastClusterLockWrap hazelcastClusterLock(HazelcastAdapter hazelcastAdapter) {
        return new HazelcastClusterLockWrap(hazelcastAdapter);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.map}'=='hazelcast'")
    public HazelcastClusterMapWrap hazelcastClusterMap(HazelcastAdapter hazelcastAdapter) {
        return new HazelcastClusterMapWrap(hazelcastAdapter);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='hazelcast'")
    public HazelcastClusterMQ hazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        return new HazelcastClusterMQ(hazelcastAdapter);
    }
}
