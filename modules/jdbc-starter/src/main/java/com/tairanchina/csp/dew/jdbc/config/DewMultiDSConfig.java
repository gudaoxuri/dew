package com.tairanchina.csp.dew.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring")
public class DewMultiDSConfig {

    private Map<String, Map<String, String>> multiDatasources;

    public Map<String, Map<String, String>> getMultiDatasources() {
        return multiDatasources;
    }

    public void setMultiDatasources(Map<String, Map<String, String>> multiDatasources) {
        this.multiDatasources = multiDatasources;
    }

}

