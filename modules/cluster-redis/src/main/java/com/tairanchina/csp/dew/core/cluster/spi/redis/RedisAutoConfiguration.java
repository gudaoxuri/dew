package com.tairanchina.csp.dew.core.cluster.spi.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(RedisTemplate.class)
public class RedisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisAutoConfiguration.class);

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.cache}'=='redis' ")
    public RedisClusterCache redisClusterCache(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterCache(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.dist}'=='redis'")
    public RedisClusterDist redisClusterDist(@Autowired RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterDist(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='redis'")
    public RedisClusterMQ redisClusterMQ(@Autowired RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterMQ(redisTemplate);
    }


}
