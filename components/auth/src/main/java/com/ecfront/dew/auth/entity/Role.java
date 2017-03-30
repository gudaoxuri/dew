package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.IdEntity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "dew_role")
public class Role extends IdEntity {

    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String tenantCode;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "dew_rel_role_resource",
            joinColumns = {@JoinColumn(name = "role_code", referencedColumnName = "code")},
            inverseJoinColumns = {@JoinColumn(name = "resource_code", referencedColumnName = "code")})
    private Set<Resource> resources;

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
