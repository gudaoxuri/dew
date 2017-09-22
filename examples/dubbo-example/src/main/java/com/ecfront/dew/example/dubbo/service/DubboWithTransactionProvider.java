package com.ecfront.dew.example.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.ecfront.dew.common.$;
import org.springframework.transaction.annotation.Transactional;

/**
 * 带声明式事务的Dubbo服务，需要指定interfaceName
 */
@Service(version = "1.0.0",interfaceName = "DubboWithTransactionAPI")
public class DubboWithTransactionProvider implements DubboWithTransactionAPI {

    @Override
    @Transactional
    public String saveRandomId(String id) {
        // Save to DB
        return id+"saved";
    }

}
