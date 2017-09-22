package com.ecfront.dew.example.cache.dto;

import java.io.Serializable;
import java.util.Date;

public class CacheExampleDTO implements Serializable {

    private String id;
    private String name;
    private Date createTime = new Date();

    public String getId() {
        return id;
    }

    public CacheExampleDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CacheExampleDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public CacheExampleDTO setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
