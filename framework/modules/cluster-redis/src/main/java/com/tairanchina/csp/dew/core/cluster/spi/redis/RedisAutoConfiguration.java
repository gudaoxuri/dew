package com.tairanchina.csp.dew.core.cluster.spi.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='redis' || '${dew.cluster.mq}'=='redis' || '${dew.cluster.lock}'=='redis' || '${dew.cluster.map}'=='redis' || '${dew.cluster.election}'=='redis'}")
public class RedisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisAutoConfiguration.class);

    @Value("${dew.cluster.config.election-period-sec:60}")
    private int electionPeriodSec;

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.cache}'=='redis'")
    public RedisClusterCache redisClusterCache(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterCache(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.lock}'=='redis'")
    public RedisClusterLockWrap redisClusterLock(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterLockWrap(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.map}'=='redis'")
    public RedisClusterMapWrap redisClusterMap(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterMapWrap(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='redis'")
    public RedisClusterMQ redisClusterMQ(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterMQ(redisTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.election}'=='redis'")
    public RedisClusterElectionWrap redisClusterElection(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterElectionWrap(redisTemplate,electionPeriodSec);
    }

}
