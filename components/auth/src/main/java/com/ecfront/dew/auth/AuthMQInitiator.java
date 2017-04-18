package com.ecfront.dew.auth;


import com.ecfront.dew.core.Dew;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AuthMQInitiator extends MQInitiator {

    @PostConstruct
    public void init() {
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_TENANT_ADD));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_TENANT_REMOVE));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_RESOURCE_ADD));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_ROLE_ADD));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_ROLE_REMOVE));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_ACCOUNT_ADD));
        amqpAdmin.declareExchange(new FanoutExchange(Dew.Constant.MQ_AUTH_ACCOUNT_REMOVE));
        Queue queue = new Queue(Dew.Constant.MQ_AUTH_REFRESH);
        amqpAdmin.declareQueue(queue);
    }

}
