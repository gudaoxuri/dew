package com.tairanchina.csp.dew.core.cluster;

public interface ClusterMapWrap {

    <M> ClusterMap<M> map(String key, Class<M> clazz);

}
