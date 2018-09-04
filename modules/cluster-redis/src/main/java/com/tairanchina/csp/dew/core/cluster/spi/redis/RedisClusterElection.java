package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.core.cluster.AbsClusterElection;
import com.tairanchina.csp.dew.core.cluster.Cluster;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisCommands;


public class RedisClusterElection extends AbsClusterElection {

    private static final String DEFAULT_KEY = "_";
    private static final int DELETE_ELECTION_PERIOD_SEC = 60;

    private String key;
    private int electionPeriodSec;
    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterElection(RedisTemplate<String, String> redisTemplate) {
        this(DEFAULT_KEY, redisTemplate);
    }

    public RedisClusterElection(String key, RedisTemplate<String, String> redisTemplate) {
        this(key, DELETE_ELECTION_PERIOD_SEC, redisTemplate);
    }

    public RedisClusterElection(String key, int electionPeriodSec, RedisTemplate<String, String> redisTemplate) {
        this.key = "dew:election:" + key;
        this.electionPeriodSec = electionPeriodSec;
        this.redisTemplate = redisTemplate;
        election();
    }

    @Override
    public void election()  {
        $.timer.periodic(electionPeriodSec, false, this::doElection);
    }

    private void doElection() {
        logger.trace("[Election] electing...");
        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
        String res = ((JedisCommands) redisConnection.getNativeConnection()).set(key, Cluster.CLASS_LOAD_UNIQUE_FLAG, "NX", "PX", electionPeriodSec + 1);
        redisConnection.close();
        if ("OK".equalsIgnoreCase(res)) {
            leader.set(true);
        } else {
            leader.set(redisTemplate.opsForValue().get(key).equals(Cluster.CLASS_LOAD_UNIQUE_FLAG));
        }
    }

}