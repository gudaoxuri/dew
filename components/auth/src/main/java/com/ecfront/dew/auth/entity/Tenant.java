package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.Code;
import com.ecfront.dew.core.entity.SafeStatusEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dew_tenant")
@ApiModel("租户实体")
public class Tenant extends SafeStatusEntity {

    @Code
    @ApiModelProperty("编码")
    @Column(nullable = false, unique = true)
    private String code;
    @ApiModelProperty("显示名称")
    @Column(nullable = false)
    private String name;
    @ApiModelProperty("图片")
    @Column(nullable = false)
    private String image;
    @ApiModelProperty("分类")
    @Column(nullable = false)
    private String category;

    public static Tenant build(String name) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setImage("");
        tenant.setCategory("");
        return tenant;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
