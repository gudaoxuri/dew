package com.ecfront.dew.core.entity;

import io.swagger.annotations.ApiModelProperty;

public abstract class SafeStatusEntity<P> extends SafeEntity<P> {

    @ApiModelProperty("是否启用")
    @EnabledColumn
    protected Boolean enabled = true;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
