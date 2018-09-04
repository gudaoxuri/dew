package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.cluster.ClusterLockWrap;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisClusterLockWrap implements ClusterLockWrap {

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterLockWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ClusterLock lock(String key) {
        return new RedisClusterLock(key, redisTemplate);
    }

}
