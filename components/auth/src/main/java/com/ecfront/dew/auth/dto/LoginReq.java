package com.ecfront.dew.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "登录请求", description = "登录ID/手机号/邮箱三者必须填写其一")
public class LoginReq {

    @ApiModelProperty("登录ID")
    private String loginId = "";
    @ApiModelProperty("手机号")
    private String mobile = "";
    @ApiModelProperty("邮箱")
    private String email = "";
    @ApiModelProperty(value = "密码", required = true)
    private String password = "";
    @ApiModelProperty(value = "验证码", notes = "需要验证码时填写")
    private String captcha;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
