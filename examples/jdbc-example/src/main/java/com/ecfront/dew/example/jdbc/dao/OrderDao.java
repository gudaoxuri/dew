package com.ecfront.dew.example.jdbc.dao;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.jdbc.annotations.Param;
import com.ecfront.dew.jdbc.annotations.Select;
import com.ecfront.dew.jdbc.DewDao;
import com.ecfront.dew.jdbc.annotations.Param;
import com.ecfront.dew.jdbc.annotations.Select;
import com.ecfront.dew.example.jdbc.entity.Order;

public interface OrderDao extends DewDao<Integer, Order> {

    @Select(value = "SELECT ord.* FROM t_order ord " +
            "INNER JOIN pet p ON p.id = ord.pet_id " +
            "WHERE p.type = #{petType}",entityClass = Order.class)
    Page<Order> findOrders(@Param("petType") String petType,
                           @Param("pageNumber") long pageNumber, @Param("pageSize") int pageSize);

}
