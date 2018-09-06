package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class HazelcastAdapter {

    private HazelcastConfig hazelcastConfig;

    private HazelcastInstance hazelcastInstance;
    private boolean active;

    public HazelcastAdapter(HazelcastConfig hazelcastConfig) {
        this.hazelcastConfig = hazelcastConfig;
    }

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

    public HazelcastInstance getHazelcastInstance() {
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
