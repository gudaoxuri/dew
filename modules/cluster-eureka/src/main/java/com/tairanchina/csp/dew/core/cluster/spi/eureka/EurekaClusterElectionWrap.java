package com.tairanchina.csp.dew.core.cluster.spi.eureka;

import com.tairanchina.csp.dew.core.cluster.ClusterElection;
import com.tairanchina.csp.dew.core.cluster.ClusterElectionWrap;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class EurekaClusterElectionWrap implements ClusterElectionWrap {

    private int electionPeriodSec;

    private String applicationName;

    private DiscoveryClient discoveryClient;

    private EurekaRegistration eurekaRegistration;

    EurekaClusterElectionWrap(int electionPeriodSec, String applicationName, DiscoveryClient discoveryClient, EurekaRegistration eurekaRegistration) {
        this.electionPeriodSec = electionPeriodSec;
        this.applicationName = applicationName;
        this.discoveryClient = discoveryClient;
        this.eurekaRegistration = eurekaRegistration;
    }

    @Override
    public ClusterElection election() {
        return new EurekaClusterElection(electionPeriodSec, applicationName, discoveryClient, eurekaRegistration);
    }

    @Override
    public ClusterElection election(String key) {
        throw new NotImplementedException();
    }

}
