package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.Code;
import com.ecfront.dew.core.entity.SafeStatusEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "dew_account")
@ApiModel("账号实体")
public class Account extends SafeStatusEntity {

    @Code
    @Column(nullable = false)
    @ApiModelProperty("编码")
    private String code;
    @ApiModelProperty("登录ID")
    @Column(nullable = false, unique = true)
    private String loginId;
    @ApiModelProperty("手机号")
    @Column(nullable = false, unique = true)
    private String mobile;
    @ApiModelProperty("邮箱")
    @Column(nullable = false, unique = true)
    private String email;
    @ApiModelProperty("密码")
    @Column(nullable = false)
    private String password;
    @ApiModelProperty("姓名")
    @Column(nullable = false)
    private String name;
    @ApiModelProperty(value = "扩展信息",notes = "Json格式")
    @Column(nullable = false)
    @Lob
    private String ext;
    @ApiModelProperty("对应的角色列表")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dew_rel_account_role",
            joinColumns = {@JoinColumn(name = "account_code", referencedColumnName = "code")},
            inverseJoinColumns = {@JoinColumn(name = "role_code", referencedColumnName = "code")})
    private Set<Role> roles;

    public static Account build(String loginId, String mobile, String email, String password, String name, Set<String> roleCodes) {
        Account account = new Account();
        account.loginId = loginId;
        account.mobile = mobile;
        account.email = email;
        account.password = password;
        account.name = name;
        account.ext = "";
        account.roles = roleCodes.stream().map(c -> {
            Role role = new Role();
            role.setCode(c);
            return role;
        }).collect(Collectors.toSet());
        return account;
    }

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

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
