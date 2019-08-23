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

package ms.dew.auth.sdk;

import ms.dew.core.auth.dto.OptInfo;

/**
 * 操作用户信息，支持多租户.
 *
 * @author gudaoxuri
 */
public class TokenInfo extends OptInfo<TokenInfo> {

    private String name;
    private String tenantId;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public TokenInfo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets tenant id.
     *
     * @return the tenant id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets tenant id.
     *
     * @param tenantId the tenant id
     * @return the tenant id
     */
    public TokenInfo setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
