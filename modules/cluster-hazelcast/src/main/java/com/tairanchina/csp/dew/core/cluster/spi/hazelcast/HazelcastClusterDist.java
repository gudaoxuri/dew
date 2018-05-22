package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.tairanchina.csp.dew.core.cluster.ClusterDistMap;
import com.tairanchina.csp.dew.core.cluster.ClusterDist;
import com.tairanchina.csp.dew.core.cluster.ClusterDistLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

public class HazelcastClusterDist implements ClusterDist {

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterDist(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public ClusterDistLock lock(String key) {
        return new HazelcastClusterDistLock(key, hazelcastAdapter.getHazelcastInstance());
    }

    @Override
    public <M> ClusterDistMap<M> map(String key, Class<M> clazz) {
        return new HazelcastClusterDistMap<>(key, hazelcastAdapter.getHazelcastInstance());
    }

}
