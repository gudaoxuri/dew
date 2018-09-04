package com.tairanchina.csp.dew.core.cluster;

public interface ClusterMapWrap {

    <M> ClusterMap<M> instance(String key, Class<M> clazz);

}
