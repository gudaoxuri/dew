package com.tairanchina.csp.dew.core.cluster;

public interface ClusterLockWrap {

    ClusterLock lock(String key);
}
