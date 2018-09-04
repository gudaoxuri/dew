package com.tairanchina.csp.dew.core.cluster.test;

import com.tairanchina.csp.dew.core.cluster.ClusterElection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterElectionTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterElectionTest.class);

    public void test(ClusterElection election) throws InterruptedException {
        Thread.sleep(35000);
        assert election.isLeader();
    }

}
