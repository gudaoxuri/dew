package com.ecfront.dew.example.sharding.entity;


import com.ecfront.dew.jdbc.entity.Column;
import com.ecfront.dew.jdbc.entity.Entity;
import com.ecfront.dew.jdbc.entity.PkColumn;

@Entity(tableName = "t_order")
public class TOrder {

    @PkColumn(uuid = true)
    private String id;

    @Column
    private long orderId;

    @Column
    private int userId;

    @Column
    private String status;

    public String getId() {
        return id;
    }

    public TOrder setId(String id) {
        this.id = id;
        return this;
    }

    public long getOrderId() {
        return orderId;
    }

    public TOrder setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public TOrder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public TOrder setStatus(String status) {
        this.status = status;
        return this;
    }
}
