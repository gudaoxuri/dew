package com.ecfront.dew.core.cluster.spi.redis;

import com.ecfront.dew.core.cluster.ClusterCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='redis' || '${dew.cluster.mq}'=='redis' || '${dew.cluster.dist}'=='redis'}")
public class RedisClusterCache implements ClusterCache {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value) {
        set(key, value, 0);
    }

    @Override
    public void set(String key, String value, int expireSec) {
        redisTemplate.opsForValue().set(key, value);
        if (expireSec != 0) {
            expire(key, expireSec);
        }
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }


    @Override
    public void lmset(String key, List<String> values) {
        lmset(key, values, 0);
    }

    @Override
    public void lmset(String key, List<String> values, int expireSec) {
        redisTemplate.opsForList().leftPushAll(key, values);
        if (expireSec != 0) {
            expire(key, expireSec);
        }
    }

    @Override
    public void lpush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public String lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public long llen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public List<String> lget(String key) {
        return redisTemplate.opsForList().range(key, 0, llen(key));
    }

    @Override
    public void hmset(String key, Map<String, String> values) {
        hmset(key, values, 0);
    }

    @Override
    public void hmset(String key, Map<String, String> values, int expireSec) {
        redisTemplate.opsForHash().putAll(key, values);
        if (expireSec != 0) {
            expire(key, expireSec);
        }
    }

    @Override
    public void hset(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public boolean hexists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key)
                .entrySet().stream().collect(
                        Collectors.toMap(i -> (String) (i.getKey()), i -> (String) (i.getValue())));
    }

    @Override
    public void hdel(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    @Override
    public long incrBy(String key, long incrValue) {
        return redisTemplate.opsForValue().increment(key, incrValue);
    }

    @Override
    public long decrBy(String key, long decrValue) {
        return redisTemplate.opsForValue().increment(key, -decrValue);
    }

    @Override
    public void expire(String key, int expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void flushdb() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }
}
