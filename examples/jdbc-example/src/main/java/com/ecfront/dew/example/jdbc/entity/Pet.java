package com.ecfront.dew.example.jdbc.entity;

import com.ecfront.dew.jdbc.entity.*;
import com.ecfront.dew.jdbc.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pet implements Serializable {

    @PkColumn
    private int id;
    @Column(notNull = true)
    private String type;
    @Column(notNull = true)
    private BigDecimal price;
    @CreateTimeColumn
    private LocalDateTime createTime;
    @UpdateTimeColumn
    private LocalDateTime updateTime;
    @EnabledColumn
    private boolean enabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
