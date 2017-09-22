package com.ecfront.dew.core.test.dataaccess.jdbc.entity;

import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.PkEntity;

@Entity
public class BasicEntity extends PkEntity {

    @Column
    private String fieldA;
    private String fieldB;

    public String getFieldA() {
        return fieldA;
    }

    public void setFieldA(String fieldA) {
        this.fieldA = fieldA;
    }

    public String getFieldB() {
        return fieldB;
    }

    public void setFieldB(String fieldB) {
        this.fieldB = fieldB;
    }
}
