/*
package com.ecfront.dew.core.cluster.spi.hazelcast;

import ClusterCache;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class HazelcastClusterCache implements ClusterCache {

    private CachingProvider cachingProvider = Caching.getCachingProvider();
    private CacheManager cacheManager = cachingProvider.getCacheManager();


    @Override
    public boolean exists(String key) {
        return cacheManager.getCache(key) != null;
    }

    @Override
    public String get(String key) {
        Cache<String, String> cache = cacheManager.getCache(key, String.class, String.class);
        if (cache != null) {
            return cache.get("");
        } else {
            return "";
        }
    }

    @Override
    public void set(String key, String value) {
        set(key, value, 0);
    }

    @Override
    public void set(String key, String value, int expireSec) {
        MutableConfiguration<String, String> mutableConfiguration = new MutableConfiguration<>();
        if (expireSec != 0) {
            mutableConfiguration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, expireSec)));
        }
        Cache<String, String> cache = cacheManager.createCache(key, mutableConfiguration);
        cache.put("", value);
    }

    @Override
    public void del(String key) {
        cacheManager.destroyCache(key);
    }

    @Override
    public void lmset(String key, List<String> values) {
        lmset(key, values, 0);
    }

    @Override
    public void lmset(String key, List<String> values, int expireSec) {
        MutableConfiguration<String, List<String>> mutableConfiguration = new MutableConfiguration<>();
        if (expireSec != 0) {
            mutableConfiguration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, expireSec)));
        }
        Cache<String, List<String>> cache = cacheManager.createCache(key, mutableConfiguration);
        cache.put("", values);
    }

    @Override
    public void lpush(String key, String value) {
        cacheManager.getCache(key, String.class, List.class).get("").add(value);
    }

    @Override
    public String lpop(String key) {
        Cache<String, List> cache = cacheManager.getCache(key, String.class, List.class);
        List<String> value = cache.get("");
        String result = value.get(0);
        value.remove(0);
        cache.put("", value);
        return result;
    }

    @Override
    public long llen(String key) {
        return cacheManager.getCache(key, String.class, List.class).get("").size();
    }

    @Override
    public List<String> lget(String key) {
        return cacheManager.getCache(key, String.class, List.class).get("");
    }

    @Override
    public void hmset(String key, Map<String, String> values) {
        hmset(key, values, 0);
    }

    @Override
    public void hmset(String key, Map<String, String> values, int expireSec) {
        MutableConfiguration<String, String> mutableConfiguration = new MutableConfiguration<>();
        if (expireSec != 0) {
            mutableConfiguration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, expireSec)));
        }
        Cache<String, String> cache = cacheManager.createCache(key, mutableConfiguration);
        cache.putAll(values);
    }

    @Override
    public void hset(String key, String field, String value) {
        Cache<String, String> cache = cacheManager.getCache(key);
        cache.put(field,value);
    }

    @Override
    public String hget(String key, String field) {
        return (String) cacheManager.getCache(key).get(field);
    }

    @Override
    public boolean hexists(String key, String field) {
        return cacheManager.getCache(key).containsKey(field);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return cacheManager.getCache(key).
                .entrySet().stream().collect(
                        Collectors.toMap(i -> (String) (i.getKey()), i -> (String) (i.getValue())));
    }

    @Override
    public void hdel(String key, String field) {
        cacheManager.getCache(key).remove(field);
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

    }

    @Override
    public void flushdb() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }

    @PreDestroy
    private void shutdown() {
        cachingProvider.close();
    }
}
*/
