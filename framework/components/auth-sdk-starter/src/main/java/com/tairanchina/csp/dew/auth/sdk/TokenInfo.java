/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
