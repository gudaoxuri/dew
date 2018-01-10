package com.ecfront.dew.jdbc.sharding.entity;

import com.ecfront.dew.jdbc.entity.Column;
import com.ecfront.dew.jdbc.entity.Entity;
import com.ecfront.dew.jdbc.entity.PkColumn;

@Entity(tableName = "t_order_item")
public class TOrderItem {

    @PkColumn(uuid = true)
    private String id;

    @Column
    private long itemId;

    @Column
    private long orderId;

    @Column
    private int userId;

    public int getUserId() {
        return userId;
    }

    public TOrderItem setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getId() {
        return id;
    }

    public TOrderItem setId(String id) {
        this.id = id;
        return this;
    }

    public long getItemId() {
        return itemId;
    }

    public TOrderItem setItemId(long itemId) {
        this.itemId = itemId;
        return this;
    }

    public long getOrderId() {
        return orderId;
    }

    public TOrderItem setOrderId(long orderId) {
        this.orderId = orderId;
        return this;
    }
}
