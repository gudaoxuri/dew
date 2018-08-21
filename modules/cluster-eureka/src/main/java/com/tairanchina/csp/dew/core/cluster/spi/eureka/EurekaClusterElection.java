package com.tairanchina.csp.dew.core.cluster.spi.eureka;

import com.ecfront.dew.common.$;
import com.netflix.appinfo.InstanceInfo;
import com.tairanchina.csp.dew.core.cluster.ClusterElection;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Optional;


public class EurekaClusterElection implements ClusterElection {

    private static boolean leader = false;

    private int electionPeriodSec;

    private String applicationName;

    private DiscoveryClient discoveryClient;

    private EurekaRegistration eurekaRegistration;

    public EurekaClusterElection(int electionPeriodSec, String applicationName, DiscoveryClient discoveryClient, EurekaRegistration eurekaRegistration) {
        this.electionPeriodSec = electionPeriodSec;
        this.applicationName = applicationName;
        this.discoveryClient = discoveryClient;
        this.eurekaRegistration = eurekaRegistration;
    }

    @Override
    @PostConstruct
    public void election() throws Exception {
        $.timer.periodic(electionPeriodSec, false, () -> {
            Optional<InstanceInfo> firstInstanceR = discoveryClient.getInstances(applicationName).stream()
                    .map(instance -> ((EurekaDiscoveryClient.EurekaServiceInstance) instance).getInstanceInfo())
                    .filter(instance -> instance.getStatus() == InstanceInfo.InstanceStatus.UP)
                    .min(Comparator.comparingLong(inst -> inst.getLeaseInfo().getRegistrationTimestamp()));
            leader = firstInstanceR.isPresent() && eurekaRegistration.getInstanceConfig().getInstanceId().equals(firstInstanceR.get().getInstanceId());
        });
    }

    @Override
    public void quit() throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public boolean isLeader() {
        return leader;
    }

}
