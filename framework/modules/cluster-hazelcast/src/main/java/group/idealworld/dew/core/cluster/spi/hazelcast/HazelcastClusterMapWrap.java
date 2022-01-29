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

package group.idealworld.dew.core.cluster.spi.hazelcast;

import group.idealworld.dew.core.cluster.ClusterMap;
import group.idealworld.dew.core.cluster.ClusterMapWrap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式Map服务多实例封装 Hazelcast 实现.
 *
 * @author gudaoxuri
 */
public class HazelcastClusterMapWrap implements ClusterMapWrap {

    private static final ConcurrentHashMap<String, ClusterMap> MAP_CONTAINER = new ConcurrentHashMap<>();

    private HazelcastAdapter hazelcastAdapter;

    /**
     * Instantiates a new Hazelcast cluster map wrap.
     *
     * @param hazelcastAdapter the hazelcast adapter
     */
    public HazelcastClusterMapWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public <M> ClusterMap<M> instance(String key, Class<M> clazz) {
        MAP_CONTAINER.putIfAbsent(key, new HazelcastClusterMap<M>(key, hazelcastAdapter.getHazelcastInstance()));
        return MAP_CONTAINER.get(key);
    }

}
