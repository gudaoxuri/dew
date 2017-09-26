package com.ecfront.dew.core.cluster.spi.redis;

import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='redis' || '${dew.cluster.mq}'=='redis' || '${dew.cluster.dist}'=='redis'}")
public class RedisClusterDist implements ClusterDist {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ClusterDistLock lock(String key) {
        return new RedisClusterDistLock(key, redisTemplate);
    }

    @Override
    public <M> ClusterDistMap<M> map(String key, Class<M> clazz) {
        return new RedisClusterDistMap<>(key, clazz, redisTemplate);
    }


}
