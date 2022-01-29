/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster.spi.redis;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.cluster.ClusterMap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分布式Map服务 Redis 实现.
 *
 * @param <M> 值的类型
 * @author gudaoxuri
 */
public class RedisClusterMap<M> implements ClusterMap<M> {

    private String mapKey;
    private Class<M> clazz;
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Instantiates a new Redis cluster map.
     *
     * @param mapKey        the map key
     * @param clazz         the clazz
     * @param redisTemplate the redis template
     */
    RedisClusterMap(String mapKey, Class<M> clazz, RedisTemplate<String, String> redisTemplate) {
        this.mapKey = "dew:cluster:map:" + mapKey;
        this.clazz = clazz;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(String key, M value) {
        redisTemplate.opsForHash().put(mapKey, key, $.json.toJsonString(value));
    }

    @Override
    public void putAsync(String key, M value) {
        new Thread(() -> put(key, value)).start();
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
            return map.entrySet().stream().collect(Collectors.toMap(i -> (String) (i.getKey()), i -> $.json.toObject(i.getValue(), clazz)));
        } else {
            return null;
        }
    }

    @Override
    public M get(String key) {
        Object result = redisTemplate.opsForHash().get(mapKey, key);
        if (result != null) {
            return $.json.toObject(result, clazz);
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
        new Thread(() -> remove(key)).start();
    }

    @Override
    public void clear() {
        redisTemplate.delete(mapKey);
    }

}
