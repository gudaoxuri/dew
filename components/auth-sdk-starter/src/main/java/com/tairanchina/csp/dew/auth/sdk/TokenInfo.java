package com.tairanchina.csp.dew.auth.sdk;

import com.tairanchina.csp.dew.core.auth.dto.OptInfo;

import java.util.Map;

public class TokenInfo extends OptInfo<TokenInfo> {

    private String name;
    private Map<String, String> roles;
    private String tenantId;

    public String getName() {
        return name;
    }

    public TokenInfo setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    public TokenInfo setRoles(Map<String, String> roles) {
        this.roles = roles;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public TokenInfo setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
