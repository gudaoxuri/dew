package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.cluster.ClusterLockWrap;

import java.util.concurrent.ConcurrentHashMap;

public class HazelcastClusterLockWrap implements ClusterLockWrap {

    private static final ConcurrentHashMap<String, ClusterLock> LOCK_CONTAINER = new ConcurrentHashMap<>();

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterLockWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public ClusterLock instance(String key) {
        LOCK_CONTAINER.putIfAbsent(key, new HazelcastClusterLock(key, hazelcastAdapter.getHazelcastInstance()));
        return LOCK_CONTAINER.get(key);
    }

}
