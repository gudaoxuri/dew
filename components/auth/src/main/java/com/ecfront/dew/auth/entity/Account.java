package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.Code;
import com.ecfront.dew.core.entity.SafeStatusEntity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "dew_account")
public class Account extends SafeStatusEntity {

    @Code
    @Column(nullable = false,unique = true)
    private String code;
    @Column(nullable = false)
    private String mobile;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Lob
    private String ext;
    @Column(nullable = false)
    private String tenantCode;
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "dew_rel_account_role",
            joinColumns = {@JoinColumn(name = "account_code", referencedColumnName = "code")},
            inverseJoinColumns = {@JoinColumn(name = "role_code", referencedColumnName = "code")})
    private Set<Role> roles;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
