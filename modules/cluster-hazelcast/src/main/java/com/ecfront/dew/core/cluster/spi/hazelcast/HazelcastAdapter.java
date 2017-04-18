package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HazelcastAdapter {

    @Autowired
    private HazelcastConfig hazelcastConfig;

    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void init() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty("hazelcast.logging.type", "slf4j");
        if(hazelcastConfig.getUserName()!=null){
            clientConfig.getGroupConfig().setName(hazelcastConfig.getUserName()).setPassword(hazelcastConfig.getPassword());
        }
        hazelcastConfig.getAddresses().forEach(i -> clientConfig.getNetworkConfig().addAddress(i));
        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }
}
