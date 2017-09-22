package com.ecfront.dew.example.jdbc.entity;

import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.PkColumn;

import java.io.Serializable;

@Entity(tableName = "t_order")
public class Order implements Serializable {

    @PkColumn
    private int id;
    @Column(notNull = true)
    private int petId;
    @Column(notNull = true)
    private int customerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
