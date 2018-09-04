package com.tairanchina.csp.dew.core.auth.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "登录信息")
public class OptInfo<E> {

    @ApiModelProperty(value = "Token", required = true)
    protected String token;
    @ApiModelProperty(value = "账号编码", required = true)
    protected Object accountCode;

    public String getToken() {
        return token;
    }

    public E setToken(String token) {
        this.token = token;
        return (E) this;
    }

    public Object getAccountCode() {
        return accountCode;
    }

    public E setAccountCode(Object accountCode) {
        this.accountCode = accountCode;
        return (E) this;
    }
}
