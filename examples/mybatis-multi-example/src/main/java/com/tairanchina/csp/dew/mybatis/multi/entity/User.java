package com.tairanchina.csp.dew.mybatis.multi.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.tairanchina.csp.dew.jdbc.entity.Column;
import com.tairanchina.csp.dew.jdbc.entity.Entity;
import com.tairanchina.csp.dew.jdbc.entity.PkColumn;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 */
@SuppressWarnings("serial")
@Entity
public class User extends Model<User> {

    /**
     * 主键ID
     */
    @TableId("test_id")
    @PkColumn(columnName = "test_id")
    private Long id;

    /**
     * 名称
     */
    @Column
    @TableField("name")
    private String name;

    /**
     * 年龄
     */
    @Column
    @TableField("age")
    private Integer age;

    @TableField("test_type")
    @Column
    private Integer testType;

    @TableField("test_date")
    @Column
    private Date testDate;

    @TableField("role")
    @Column
    private Long role;
    @TableField("phone")
    @Column
    private String phone;

    public User() {
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

    public User(Long id, String name, Integer age, Integer testType) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public User(String name, Integer age, Integer testType) {
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getTestType() {
        return this.testType;
    }

    public void setTestType(Integer testType) {
        this.testType = testType;
    }

    public Long getRole() {
        return this.role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", age=" + age
                + ", testType=" + testType + ", testDate="
                + testDate + ", role=" + role + ", phone=" + phone + "]";
    }


}
