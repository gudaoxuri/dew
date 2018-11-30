package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import com.tairanchina.csp.dew.core.cluster.ClusterMapWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

public class RedisClusterMapWrap implements ClusterMapWrap {

    private static final ConcurrentHashMap<String, ClusterMap> MAP_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterMapWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <M> ClusterMap<M> instance(String key, Class<M> clazz) {
        MAP_CONTAINER.putIfAbsent(key, new RedisClusterMap<>(key, clazz, redisTemplate));
        return MAP_CONTAINER.get(key);
    }

}
