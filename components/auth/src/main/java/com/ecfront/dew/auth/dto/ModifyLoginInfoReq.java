package com.ecfront.dew.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "修改登录信息请求")
public class ModifyLoginInfoReq {

    @ApiModelProperty("登录ID")
    private String loginId = "";
    @ApiModelProperty("手机号")
    private String mobile = "";
    @ApiModelProperty("邮箱")
    private String email = "";
    @ApiModelProperty("姓名")
    private String name = "";
    @ApiModelProperty(value = "原密码", required = true)
    private String oldPassword = "";
    @ApiModelProperty(value = "新密码")
    private String newPassword = "";

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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
