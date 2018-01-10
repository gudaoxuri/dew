package com.ecfront.dew.jdbc.entity;


import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

public abstract class SafeEntity<P> extends PkEntity<P> {

    @ApiModelProperty("创建时间")
    @CreateTimeColumn
    protected LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @UpdateTimeColumn
    protected LocalDateTime updateTime;

    @ApiModelProperty("创建人编码")
    @CreateUserColumn
    protected String createUser;

    @ApiModelProperty("更新人编码")
    @UpdateUserColumn
    protected String updateUser;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
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
