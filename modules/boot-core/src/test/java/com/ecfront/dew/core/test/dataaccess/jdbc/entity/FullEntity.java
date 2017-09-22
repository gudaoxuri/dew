package com.ecfront.dew.core.test.dataaccess.jdbc.entity;

import com.ecfront.dew.core.entity.CodeColumn;
import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.SafeStatusEntity;

@Entity(tableName = "full_entity")
public class FullEntity extends SafeStatusEntity<Integer> {

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
