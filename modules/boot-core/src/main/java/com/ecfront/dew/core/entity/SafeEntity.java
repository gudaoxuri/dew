package com.ecfront.dew.core.entity;


import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public abstract class SafeEntity<P> extends PkEntity<P> {

    @ApiModelProperty("创建时间")
    @CreateTimeColumn
    protected Date createTime;

    @ApiModelProperty("更新时间")
    @UpdateTimeColumn
    protected Date updateTime;

    @ApiModelProperty("创建人编码")
    @CreateUserColumn
    protected String createUser;

    @ApiModelProperty("更新人编码")
    @UpdateUserColumn
    protected String updateUser;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

}
