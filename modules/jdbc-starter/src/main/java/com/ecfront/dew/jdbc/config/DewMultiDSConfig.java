package com.ecfront.dew.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
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

