package your.group.domain;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.core.entity.Column;
import com.ecfront.dew.core.entity.Entity;
import com.ecfront.dew.core.entity.PkColumn;
import com.ecfront.dew.core.jdbc.DewDao;

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

    public static ActiveRecord $$ = new ActiveRecord();

    public static class ActiveRecord implements DewDao<Integer, Order> {

        public Page<Order> findOrders(int customerId, String petType, long pageNumber, int pageSize) {
            return getDS().paging("SELECT ord.* FROM t_order ord " +
                            "INNER JOIN pet p ON p.id = ord.pet_id " +
                            "WHERE ord.customer_id = ? AND p.type = ?",
                    new Object[]{customerId, petType}, pageNumber, pageSize, Order.class);
        }

    }
}
