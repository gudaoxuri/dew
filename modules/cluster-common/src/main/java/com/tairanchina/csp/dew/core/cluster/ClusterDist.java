package com.tairanchina.csp.dew.core.cluster;

public interface ClusterDist {

    ClusterDistLock lock(String key);

    <M> ClusterDistMap<M> map(String key,Class<M> clazz);

}
