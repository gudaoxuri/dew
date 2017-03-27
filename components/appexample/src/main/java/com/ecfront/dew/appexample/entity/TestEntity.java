package com.ecfront.dew.appexample.entity;

import com.ecfront.dew.core.entity.SafeStatusEntity;

import javax.persistence.Entity;

@Entity
public class TestEntity extends SafeStatusEntity {

    private String token;
    private String message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
