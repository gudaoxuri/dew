package com.trc.dew.crud.entity;

import com.tairanchina.csp.dew.jdbc.entity.CodeColumn;
import com.tairanchina.csp.dew.jdbc.entity.Column;
import com.tairanchina.csp.dew.jdbc.entity.Entity;
import com.tairanchina.csp.dew.jdbc.entity.SafeStatusEntity;

@Entity(tableName = "test_select_entity")
public class TestSelectEntity extends SafeStatusEntity<Integer> {

    @CodeColumn
    private String code;
    @Column
    private String fieldA;
    @Column(columnName = "field_c", notNull = true)
    private String fieldB;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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
