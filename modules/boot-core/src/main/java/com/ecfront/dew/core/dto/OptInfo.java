package com.ecfront.dew.core.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel(value = "登录信息")
public class OptInfo {

    @ApiModelProperty(value = "Token", required = true)
    private String token;
    @ApiModelProperty(value = "账号编码", required = true)
    private String accountCode;
    @ApiModelProperty(value = "登录ID", required = true)
    private String loginId;
    @ApiModelProperty(value = "手机号", required = true)
    private String mobile;
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;
    @ApiModelProperty(value = "姓名", required = true)
    private String name;
    @ApiModelProperty(value = "角色列表", required = true)
    private List<RoleInfo> roles;
    @ApiModelProperty(value = "最后一次登录时间", required = true)
    private Date lastLoginTime;
    @ApiModelProperty(value = "扩展信息(Json格式)", required = true)
    private String ext;

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

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTenantCode() {
            return tenantCode;
        }

        public void setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
