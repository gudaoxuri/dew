package com.ecfront.dew.core.cluster.spi.ignite;

import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IgniteClusterDist implements ClusterDist {

    @Autowired
    private IgniteAdapter igniteAdapter;

    @Override
    public ClusterDistLock lock(String key) {
        return new IgniteClusterDistLock(key, igniteAdapter.getIgnite());
    }

    @Override
    public <M> ClusterDistMap<M> map(String key, Class<M> clazz) {
        return new IgniteClusterDistMap<>(key, igniteAdapter.getIgnite());
    }

}
