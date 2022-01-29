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

import group.idealworld.dew.core.cluster.ClusterLock;
import group.idealworld.dew.core.cluster.ClusterLockWrap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁服务多实例封装 Hazelcast 实现.
 *
 * @author gudaoxuri
 */
public class HazelcastClusterLockWrap implements ClusterLockWrap {

    private static final ConcurrentHashMap<String, ClusterLock> LOCK_CONTAINER = new ConcurrentHashMap<>();

    private HazelcastAdapter hazelcastAdapter;

    /**
     * Instantiates a new Hazelcast cluster lock wrap.
     *
     * @param hazelcastAdapter the hazelcast adapter
     */
    public HazelcastClusterLockWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public ClusterLock instance(String key) {
        LOCK_CONTAINER.putIfAbsent(key, new HazelcastClusterLock(key, hazelcastAdapter.getHazelcastInstance()));
        return LOCK_CONTAINER.get(key);
    }

}
