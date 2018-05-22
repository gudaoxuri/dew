package com.tairanchina.csp.dew.jdbc.entity;

import io.swagger.annotations.ApiModelProperty;

public abstract class StatusEntity<P> extends PkEntity<P> {

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
