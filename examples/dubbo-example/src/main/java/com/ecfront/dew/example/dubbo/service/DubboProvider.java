package com.ecfront.dew.example.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.ecfront.dew.common.$;

@Service(version = "1.0.0")
public class DubboProvider implements DubboAPI {

    @Override
    public String getRandomId() {
        return $.field.createShortUUID();
    }

}
