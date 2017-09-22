package your.group.entity;

import com.ecfront.dew.core.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Pet implements Serializable {

    @PkColumn
    private int id;
    @Column(notNull = true)
    private String type;
    @Column(notNull = true)
    private BigDecimal price;
    @CreateTimeColumn
    private Date createTime;
    @UpdateTimeColumn
    private Date updateTime;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
