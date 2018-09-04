package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.cluster.ClusterLockWrap;

public class HazelcastClusterLockWrap implements ClusterLockWrap {

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterLockWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public ClusterLock instance(String key) {
        return new HazelcastClusterLock(key, hazelcastAdapter.getHazelcastInstance());
    }

}
