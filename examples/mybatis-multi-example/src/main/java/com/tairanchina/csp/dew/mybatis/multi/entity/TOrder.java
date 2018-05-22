package com.tairanchina.csp.dew.mybatis.multi.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.tairanchina.csp.dew.jdbc.entity.Column;
import com.tairanchina.csp.dew.jdbc.entity.Entity;
import com.tairanchina.csp.dew.jdbc.entity.PkColumn;

@Entity(tableName = "t_order")
@TableName("t_order")
public class TOrder {

    @PkColumn(uuid = true)         // Dew备注
    @TableId(type = IdType.UUID)   // Mybatis备注
    private String id;

    @Column
    @TableField("order_id")
    private long orderId;

    @Column
    @TableField("user_id")
    private int userId;

    @Column
    @TableField
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
