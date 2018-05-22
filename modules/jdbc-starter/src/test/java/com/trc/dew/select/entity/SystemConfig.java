package com.trc.dew.select.entity;


import com.tairanchina.csp.dew.jdbc.entity.Column;
import com.tairanchina.csp.dew.jdbc.entity.Entity;
import com.tairanchina.csp.dew.jdbc.entity.SafeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity(tableName = "system_config")
@ApiModel("系统配置")
public class SystemConfig extends SafeEntity<String > {

    @Column
    @ApiModelProperty("参数数值")
    private String value;
    @Column
    @ApiModelProperty("参数说明")
    private String description;
    @Column
    @ApiModelProperty("级别（字典）")
    private String level;

    public String getValue() {
        return value;
    }

    public SystemConfig setValue(String value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SystemConfig setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public SystemConfig setLevel(String level) {
        this.level = level;
        return this;
    }


}
