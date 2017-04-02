package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.Code;
import com.ecfront.dew.core.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dew_resource")
@ApiModel("资源实体")
public class Resource extends IdEntity {

    public static final String CATEGORY_DEFAULT = "";
    public static final String CATEGORY_MENU = "menu";

    @Code
    @ApiModelProperty("编码")
    @Column(nullable = false, unique = true)
    private String code;
    @ApiModelProperty("URL")
    @Column(nullable = false)
    private String uri;
    @ApiModelProperty(value = "方法名", notes = "*表示所有方法")
    @Column(nullable = false)
    private String method;
    @ApiModelProperty("显示名称")
    @Column(nullable = false)
    private String name;
    @ApiModelProperty(value = "类别", notes = "为空表示表示默认类型，menu表示菜单类型")
    @Column(nullable = false)
    private String category;
    @ApiModelProperty("显示图标")
    @Column(nullable = false)
    private String icon;
    @ApiModelProperty("父编码")
    @Column(nullable = false)
    private String parentCode;
    @ApiModelProperty("显示排序")
    @Column(nullable = false)
    private int sort;
    @ApiModelProperty("租户编码")
    @Column(nullable = false)
    private String tenantCode;

    public static Resource build(String uri, String method, String name, String tenantCode) {
        Resource resource = new Resource();
        resource.uri = uri;
        resource.method = method;
        resource.name = name;
        resource.category = CATEGORY_DEFAULT;
        resource.icon = "";
        resource.parentCode = "";
        resource.tenantCode = tenantCode;
        return resource;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
}
