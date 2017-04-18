package com.ecfront.dew.core.cluster;

public interface ClusterDist {

    ClusterDistLock lock(String key);

    <M> ClusterDistMap<M> map(String key);

}
