package com.tairanchina.csp.dew.core.cluster;

public interface ClusterElectionWrap {

    ClusterElection instance();

    ClusterElection instance(String key);

}
