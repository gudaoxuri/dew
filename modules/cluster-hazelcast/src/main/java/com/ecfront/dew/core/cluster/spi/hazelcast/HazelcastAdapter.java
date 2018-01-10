package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='hazelcast' || '${dew.cluster.mq}'=='hazelcast' || '${dew.cluster.dist}'=='hazelcast'}")
public class HazelcastAdapter {

    @Autowired
    private HazelcastConfig hazelcastConfig;

    private HazelcastInstance hazelcastInstance;
    private boolean active;

    @PostConstruct
    public void init() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty("hazelcast.logging.type", "slf4j");
        if (hazelcastConfig.getUserName() != null) {
            clientConfig.getGroupConfig().setName(hazelcastConfig.getUserName()).setPassword(hazelcastConfig.getPassword());
        }
        clientConfig.getNetworkConfig().setConnectionTimeout(hazelcastConfig.getConnectionTimeout());
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(hazelcastConfig.getConnectionAttemptLimit());
        clientConfig.getNetworkConfig().setConnectionAttemptPeriod(hazelcastConfig.getConnectionAttemptPeriod());
        hazelcastConfig.getAddresses().forEach(i -> clientConfig.getNetworkConfig().addAddress(i));
        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
        active=true;
    }

    HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    boolean isActive(){
        return active;
    }

    @PreDestroy
    public void shutdown(){
        active=false;
        hazelcastInstance.shutdown();
    }
}
