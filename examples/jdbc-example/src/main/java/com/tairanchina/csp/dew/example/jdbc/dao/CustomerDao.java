package com.tairanchina.csp.dew.example.jdbc.dao;


import com.tairanchina.csp.dew.jdbc.DewDao;
import com.tairanchina.csp.dew.example.jdbc.entity.Customer;

public interface CustomerDao extends DewDao<Integer, Customer> {
    @Override
    default String ds() {
        // 其它数据必须指时数据源的名称
        return "other";
    }
}
