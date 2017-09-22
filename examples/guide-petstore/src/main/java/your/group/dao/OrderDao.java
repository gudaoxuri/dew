package your.group.dao;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.core.jdbc.DewDao;
import com.ecfront.dew.core.jdbc.annotations.Param;
import com.ecfront.dew.core.jdbc.annotations.Select;
import your.group.entity.Order;

public interface OrderDao extends DewDao<Integer, Order> {

    @Select(value = "SELECT ord.* FROM t_order ord " +
            "INNER JOIN pet p ON p.id = ord.pet_id " +
            "WHERE ord.customer_id = #{customerId} AND p.type = #{petType}",entityClass = Order.class)
    Page<Order> findOrders(@Param("customerId") int customerId, @Param("petType") String petType,
                           @Param("pageNumber") long pageNumber, @Param("pageSize") int pageSize);

}
