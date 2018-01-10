package com.ecfront.dew.example.jdbc.dao;


import com.ecfront.dew.example.jdbc.entity.Customer;
import com.ecfront.dew.jdbc.DewDao;
import com.ecfront.dew.example.jdbc.entity.Customer;

public interface CustomerDao extends DewDao<Integer, Customer> {
    @Override
    default String ds() {
        // 其它数据必须指时数据源的名称
        return "other";
    }
}
