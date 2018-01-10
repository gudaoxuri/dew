package com.ecfront.dew.core.cluster.spi.eureka;

import com.ecfront.dew.common.$;
import com.ecfront.dew.core.cluster.ClusterElection;
import com.netflix.appinfo.InstanceInfo;
import com.ecfront.dew.core.cluster.ClusterElection;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Optional;

@Component
@ConditionalOnExpression("#{'${dew.cluster.election}'=='eureka'}")
public class EurekaClusterElection implements ClusterElection {

    private static boolean leader = false;

    @Value("${dew.cluster.election.config.election-period-sec:60}")
    private int electionPeriodSec;
    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private EurekaRegistration eurekaRegistration;

    @Override
    @PostConstruct
    public void election() throws Exception {
        $.timer.periodic(electionPeriodSec, false, () -> {
            Optional<InstanceInfo> firstInstanceR = discoveryClient.getInstances(applicationName).stream()
                    .map(instance -> ((EurekaDiscoveryClient.EurekaServiceInstance) instance).getInstanceInfo())
                    .filter(instance -> instance.getStatus() == InstanceInfo.InstanceStatus.UP)
                    .sorted(Comparator.comparingLong(inst -> inst.getLeaseInfo().getRegistrationTimestamp()))
                    .findFirst();
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
