package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterElection;
import com.tairanchina.csp.dew.core.cluster.ClusterElectionWrap;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisClusterElectionWrap implements ClusterElectionWrap {

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterElectionWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ClusterElection election() {
        return new RedisClusterElection(redisTemplate);
    }

    @Override
    public ClusterElection election(String key) {
        return new RedisClusterElection(key, redisTemplate);
    }

}
