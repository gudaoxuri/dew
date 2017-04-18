package com.ecfront.dew.core.cluster.spi.redis;

import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisClusterDist implements ClusterDist {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public ClusterDistLock lock(String key) {
        return new RedisClusterDistLock(key, redisTemplate);
    }

    @Override
    public <M> ClusterDistMap<M> map(String key,Class<M> clazz) {
        return new RedisClusterDistMap<>(key,clazz, redisTemplate);
    }


}
