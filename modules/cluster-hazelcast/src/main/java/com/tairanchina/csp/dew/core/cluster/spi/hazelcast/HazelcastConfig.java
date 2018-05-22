package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "spring.hazelcast")
public class HazelcastConfig {

    private String userName;
    private String password;
    private List<String> addresses = new ArrayList<>();
    // Timeout value in milliseconds for nodes to accept client connection requests.
    private int connectionTimeout = 5000;
    private int connectionAttemptLimit = 2;
    private int connectionAttemptPeriod = 3000;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionAttemptLimit() {
        return connectionAttemptLimit;
    }

    public void setConnectionAttemptLimit(int connectionAttemptLimit) {
        this.connectionAttemptLimit = connectionAttemptLimit;
    }

    public int getConnectionAttemptPeriod() {
        return connectionAttemptPeriod;
    }

    public void setConnectionAttemptPeriod(int connectionAttemptPeriod) {
        this.connectionAttemptPeriod = connectionAttemptPeriod;
    }
}
