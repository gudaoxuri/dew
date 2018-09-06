package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterElection;
import com.tairanchina.csp.dew.core.cluster.ClusterElectionWrap;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisClusterElectionWrap implements ClusterElectionWrap {

    private RedisTemplate<String, String> redisTemplate;
    private int electionPeriodSec;

    public RedisClusterElectionWrap(RedisTemplate<String, String> redisTemplate,int electionPeriodSec) {
        this.redisTemplate = redisTemplate;
        this.electionPeriodSec=electionPeriodSec;
    }

    @Override
    public ClusterElection instance() {
        return new RedisClusterElection(electionPeriodSec,redisTemplate);
    }

    @Override
    public ClusterElection instance(String key) {
        return new RedisClusterElection(key,electionPeriodSec, redisTemplate);
    }

}
