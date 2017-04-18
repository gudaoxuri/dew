package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HazelcastClusterDist implements ClusterDist {

    @Autowired
    private HazelcastAdapter hazelcastAdapter;

    @Override
    public ClusterDistLock lock(String key) {
        return new HazelcastClusterDistLock(key, hazelcastAdapter);
    }

    @Override
    public <M> ClusterDistMap<M> map(String key, Class<M> clazz) {
        return new HazelcastClusterDistMap<>(key, clazz, hazelcastAdapter);
    }

}
