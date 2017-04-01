package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.Code;
import com.ecfront.dew.core.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "dew_role")
public class Role extends IdEntity {

    @Code
    @ApiModelProperty("编码")
    @Column(nullable = false)
    private String code;
    @ApiModelProperty("显示名称")
    @Column(nullable = false)
    private String name;
    @ApiModelProperty("租户编码")
    @Column(nullable = false)
    private String tenantCode;
    @ApiModelProperty("对应的资源列表")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dew_rel_role_resource",
            joinColumns = {@JoinColumn(name = "role_code", referencedColumnName = "code")},
            inverseJoinColumns = {@JoinColumn(name = "resource_code", referencedColumnName = "code")})
    private Set<Resource> resources;

    public static Role build(String name, String tenantCode, Set<String> resourceCodes) {
        Role role = new Role();
        role.name = name;
        role.tenantCode = tenantCode;
        role.resources = resourceCodes.stream().map(c -> {
            Resource resource = new Resource();
            resource.setCode(c);
            return resource;
        }).collect(Collectors.toSet());
        return role;
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

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }
}
