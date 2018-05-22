package com.tairanchina.csp.dew.example.jdbc.entity;

import com.tairanchina.csp.dew.jdbc.entity.Column;
import com.tairanchina.csp.dew.jdbc.entity.Entity;
import com.tairanchina.csp.dew.jdbc.entity.PkColumn;

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
