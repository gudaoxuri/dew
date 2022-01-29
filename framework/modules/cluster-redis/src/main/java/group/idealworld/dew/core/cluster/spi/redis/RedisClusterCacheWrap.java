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

import group.idealworld.dew.core.cluster.ClusterCache;
import group.idealworld.dew.core.cluster.ClusterCacheWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存服务多实例封装 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterCacheWrap implements ClusterCacheWrap {

    private static final ConcurrentHashMap<String, ClusterCache> CACHE_CONTAINER = new ConcurrentHashMap<>();

    RedisClusterCacheWrap(Map<String, RedisTemplate<String, String>> redisTemplates) {
        redisTemplates.forEach((k, v) ->
                CACHE_CONTAINER.put(k, new RedisClusterCache(v)));
    }

    @Override
    public ClusterCache instance(String key) {
        return CACHE_CONTAINER.get(key);
    }

    @Override
    public boolean exist(String key) {
        return CACHE_CONTAINER.contains(key);
    }
}
