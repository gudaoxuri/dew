package com.tairanchina.csp.dew.core.cluster;

public interface ClusterElectionWrap {

    ClusterElection election();

    ClusterElection election(String key);

}
