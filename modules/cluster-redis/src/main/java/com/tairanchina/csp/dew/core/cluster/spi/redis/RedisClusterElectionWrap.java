package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterElection;
import com.tairanchina.csp.dew.core.cluster.ClusterElectionWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

public class RedisClusterElectionWrap implements ClusterElectionWrap {

    private static final ConcurrentHashMap<String, ClusterElection> ELECTION_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;
    private int electionPeriodSec;

    public RedisClusterElectionWrap(RedisTemplate<String, String> redisTemplate, int electionPeriodSec) {
        this.redisTemplate = redisTemplate;
        this.electionPeriodSec = electionPeriodSec;
    }

    @Override
    public ClusterElection instance() {
        ELECTION_CONTAINER.putIfAbsent("", new RedisClusterElection(electionPeriodSec, redisTemplate));
        return ELECTION_CONTAINER.get("");
    }

    @Override
    public ClusterElection instance(String key) {
        ELECTION_CONTAINER.putIfAbsent(key, new RedisClusterElection(electionPeriodSec, redisTemplate));
        return ELECTION_CONTAINER.get(key);
    }

}
