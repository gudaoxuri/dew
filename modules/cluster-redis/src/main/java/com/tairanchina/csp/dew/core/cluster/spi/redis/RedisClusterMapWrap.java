package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import com.tairanchina.csp.dew.core.cluster.ClusterMapWrap;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisClusterMapWrap implements ClusterMapWrap {

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterMapWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <M> ClusterMap<M> map(String key, Class<M> clazz) {
        return new RedisClusterMap<>(key, clazz, redisTemplate);
    }


}
