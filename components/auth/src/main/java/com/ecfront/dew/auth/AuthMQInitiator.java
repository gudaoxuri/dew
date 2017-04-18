package com.ecfront.dew.auth;


import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.core.Dew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AuthMQInitiator {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @PostConstruct
    public void init() {
        Dew.cluster.mq.response(Dew.Constant.MQ_AUTH_REFRESH, message -> {
            resourceRepository.findAll().forEach(resource -> Dew.cluster.mq.publish(Dew.Constant.MQ_AUTH_RESOURCE_ADD, JsonHelper.toJsonString(resource)));
            roleRepository.findAll().forEach(role -> Dew.cluster.mq.publish(Dew.Constant.MQ_AUTH_ROLE_ADD, JsonHelper.toJsonString(role)));
        });
    }

}
