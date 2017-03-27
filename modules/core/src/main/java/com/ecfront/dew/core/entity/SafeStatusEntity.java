package com.ecfront.dew.core.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SafeStatusEntity extends SafeEntity {

    @Column(nullable = false)
    protected Boolean enable=true;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

}
