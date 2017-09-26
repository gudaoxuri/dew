package com.ecfront.dew.core.test.crud.entity;

import com.ecfront.dew.core.entity.CodeColumn;
import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.SafeStatusEntity;
import com.ecfront.dew.core.jdbc.DewDao;

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

    public static ActiveRecord $$ = new ActiveRecord();

    public static class ActiveRecord implements DewDao<Integer, TestSelectEntity> {

    }
}
