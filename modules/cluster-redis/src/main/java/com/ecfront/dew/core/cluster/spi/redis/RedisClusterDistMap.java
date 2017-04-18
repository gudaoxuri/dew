package com.ecfront.dew.core.cluster.spi.redis;

import com.ecfront.dew.core.cluster.ClusterDistMap;
import com.ecfront.dew.common.JsonHelper;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.stream.Collectors;

public class RedisClusterDistMap<M> implements ClusterDistMap<M> {

    private String mapKey;
    private Class<M> clazz = (Class<M>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterDistMap(String mapKey, RedisTemplate<String, String> redisTemplate) {
        this.mapKey = "dew:dist:map:" + mapKey;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(String key, M value) {
        redisTemplate.opsForHash().put(mapKey, key, JsonHelper.toJsonString(value));
    }

    @Override
    public void putAsync(String key, M value) {
        new Thread(() -> put(key, value));
    }

    @Override
    public void putIfAbsent(String key, M value) {
        redisTemplate.opsForHash().putIfAbsent(mapKey, key, value);
    }

    @Override
    public boolean containsKey(String key) {
        return redisTemplate.opsForHash().hasKey(mapKey, key);
    }

    @Override
    public Map<String, M> getAll() {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(mapKey);
        if (map != null) {
            return map.entrySet().stream().collect(Collectors.toMap(i -> (String) (i.getKey()), i -> JsonHelper.toObject(i.getValue(), clazz)));
        } else {
            return null;
        }
    }

    @Override
    public M get(String key) {
        Object result = redisTemplate.opsForHash().get(mapKey, key);
        if (result != null) {
            return JsonHelper.toObject(result, clazz);
        } else {
            return null;
        }
    }

    @Override
    public void remove(String key) {
        redisTemplate.opsForHash().delete(mapKey, key);
    }

    @Override
    public void removeAsync(String key) {
        new Thread(() -> remove(key));
    }

    @Override
    public void clear() {
        redisTemplate.delete(mapKey);
    }

}
