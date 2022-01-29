/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 操作用户信息.
 *
 * @author gudaoxuri
 */
@Schema(title = "操作用户信息")
public class OptInfo {

    /**
     * The constant DEFAULT_TOKEN_KIND_FLAG.
     */
    public static final String DEFAULT_TOKEN_KIND_FLAG = "DEFAULT";

    @Schema(title = "Token", required = true)
    protected String token;

    @Schema(title = "Token类型")
    protected String tokenKind = DEFAULT_TOKEN_KIND_FLAG;

    @Schema(title = "AK", required = true)
    protected String ak;

    @Schema(title = "账号编码", required = true)
    protected String accountCode;

    @Schema(title = "应用编码", required = true)
    protected String appCode;

    @Schema(title = "租户编码", required = true)
    protected String tenantCode;

    @Schema(title = "角色列表", required = true)
    protected String[] roles;

    @Schema(title = "群组列表", required = true)
    protected String[] groups;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenKind() {
        return tokenKind;
    }

    public void setTokenKind(String tokenKind) {
        this.tokenKind = tokenKind;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }
}
