package com.tairanchina.csp.dew.auth.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "dew.component.auth.sdk")
public class AuthSDKConfig {

    public static final String HTTP_ACCESS_TOKEN = "X-Access-Token";
    public static final String HTTP_USER_TOKEN = "X-User-Token";
    public static final String HTTP_URI = "X-Uri";

    private String serverUrl = "";

    private Set<String> whiteList = new HashSet<>();

    public String getServerUrl() {
        return serverUrl;
    }

    public AuthSDKConfig setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public Set<String> getWhiteList() {
        if (!whiteList.contains("POST@/dew-auth/basic/access-token")) {
            whiteList.add("POST@/dew-auth/basic/access-token");
        }
        if (!whiteList.contains("GET@/dew-auth/basic/auth/validate")) {
            whiteList.add("GET@/dew-auth/basic/auth/validate");
        }
        return whiteList;
    }

    public AuthSDKConfig setWhiteList(Set<String> whiteList) {
        this.whiteList = whiteList;
        return this;
    }

}
