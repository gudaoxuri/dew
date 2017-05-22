package com.ecfront.dew.core.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SafeStatusEntity extends SafeEntity {

    @Column(nullable = false)
    @ApiModelProperty("是否启用")
    protected Boolean enable=true;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

}
