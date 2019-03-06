package com.tairanchina.csp.dew.core.cluster;

/**
 * 领导者选举
 */
public interface ClusterElection {

    /**
     * 当前工程是否是领导者
     *
     * @return 是否是领导者
     */
    boolean isLeader();

}
