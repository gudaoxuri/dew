package com.tairanchina.csp.dew.core.cluster.ha.dto;

public class HAConfig {

    // 容器环境下请选择持久卷
    private String storagePath = "./";
    private String storageName;
    private String authUsername;
    private String authPassword;

    public String getStoragePath() {
        return storagePath;
    }

    public HAConfig setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public String getStorageName() {
        return storageName;
    }

    public HAConfig setStorageName(String storageName) {
        this.storageName = storageName;
        return this;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public HAConfig setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
        return this;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public HAConfig setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
        return this;
    }
}
