package com.ecfront.dew.core.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel(value = "登录信息")
public class OptInfo {

    @ApiModelProperty(value = "Token", required = true)
    protected String token;
    @ApiModelProperty(value = "账号编码", required = true)
    protected String accountCode;
    @ApiModelProperty(value = "手机号", required = true)
    protected String mobile;
    @ApiModelProperty(value = "邮箱", required = true)
    protected String email;
    @ApiModelProperty(value = "姓名", required = true)
    protected String name;
    @ApiModelProperty(value = "角色列表", required = true)
    protected List<RoleInfo> roles;
    @ApiModelProperty(value = "最后一次登录时间", required = true)
    protected Date lastLoginTime;

    @ApiModel(value = "角色信息")
    public static class RoleInfo {
        @ApiModelProperty(value = "角色编码", required = true)
        private String code;
        @ApiModelProperty(value = "角色显示名称", required = true)
        private String name;
        @ApiModelProperty(value = "租户编码", required = true)
        private String tenantCode;

        public String getCode() {
            return code;
        }

        public RoleInfo setCode(String code) {
            this.code = code;
            return this;
        }

        public String getName() {
            return name;
        }

        public RoleInfo setName(String name) {
            this.name = name;
            return this;
        }

        public String getTenantCode() {
            return tenantCode;
        }

        public RoleInfo setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
            return this;
        }
    }

    public String getToken() {
        return token;
    }

    public OptInfo setToken(String token) {
        this.token = token;
        return this;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public OptInfo setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public OptInfo setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public OptInfo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public OptInfo setName(String name) {
        this.name = name;
        return this;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public OptInfo setRoles(List<RoleInfo> roles) {
        this.roles = roles;
        return this;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public OptInfo setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return this;
    }

}
