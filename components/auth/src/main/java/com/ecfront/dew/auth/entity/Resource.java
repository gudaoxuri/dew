package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.IdEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dew_resource")
public class Resource extends IdEntity {

    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String uri;
    @Column(nullable = false)
    private String method;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String icon;
    @Column(nullable = false)
    private String parentCode;
    @Column(nullable = false)
    private int sort;
    @Column(nullable = false)
    private String tenantCode;

}
