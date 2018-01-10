package com.ecfront.dew.core.cluster.spi.ignite;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnBean(IgniteAdapter.class)
@ConfigurationProperties(prefix = "spring.ignite")
public class IgniteConfig {

    private List<String> addresses = new ArrayList<>();
    private String multicastGroup = "";
    private boolean client = true;

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public void setMulticastGroup(String multicastGroup) {
        this.multicastGroup = multicastGroup;
    }

    public boolean isClient() {
        return client;
    }

    public void setClient(boolean client) {
        this.client = client;
    }
}
