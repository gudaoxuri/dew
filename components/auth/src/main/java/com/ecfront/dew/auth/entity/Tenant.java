package com.ecfront.dew.auth.entity;

import com.ecfront.dew.core.entity.SafeStatusEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dew_tenant")
public class Tenant extends SafeStatusEntity {

    private String code;
    private String name;
    private String image;
    private String category;

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
