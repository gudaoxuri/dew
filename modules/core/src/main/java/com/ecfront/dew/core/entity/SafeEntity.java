package com.ecfront.dew.core.entity;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class SafeEntity extends IdEntity {

    @Column(nullable = false)
    protected Date createTime;

    @Column(nullable = false)
    protected Date updateTime;

    @Column(nullable = false)
    protected String createUser;

    @Column(nullable = false)
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
