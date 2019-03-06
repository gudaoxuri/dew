package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.cluster.ClusterLockWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

public class RedisClusterLockWrap implements ClusterLockWrap {

    private static final ConcurrentHashMap<String, ClusterLock> LOCK_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterLockWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ClusterLock instance(String key) {
        LOCK_CONTAINER.putIfAbsent(key, new RedisClusterLock(key, redisTemplate));
        return LOCK_CONTAINER.get(key);
    }

}
