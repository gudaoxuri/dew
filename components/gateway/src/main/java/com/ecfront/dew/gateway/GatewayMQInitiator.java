package com.ecfront.dew.gateway;


import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.MQInitiator;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GatewayMQInitiator extends MQInitiator {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostConstruct
    public void init() {
        amqpTemplate.convertAndSend(Dew.Constant.MQ_AUTH_REFRESH, "");
    }

    @Bean
    public Queue mqResourceAdd() {
        Queue queue = new Queue("mqResourceAdd_" + Dew.Util.createShortUUID(), false,true,true);
        FanoutExchange ex = new FanoutExchange(Dew.Constant.MQ_AUTH_RESOURCE_ADD);
        amqpAdmin.declareExchange(ex);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(ex));
        return queue;
    }

    @Bean
    public Queue mqResourceRemove() {
        Queue queue = new Queue("mqResourceRemove_" + Dew.Util.createShortUUID(), false,true,true);
        FanoutExchange ex = new FanoutExchange(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE);
        amqpAdmin.declareExchange(ex);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(ex));
        return queue;
    }

    @Bean
    public Queue mqRoleAdd() {
        Queue queue = new Queue("mqRoleAdd_" + Dew.Util.createShortUUID(), false,true,true);
        FanoutExchange ex = new FanoutExchange(Dew.Constant.MQ_AUTH_ROLE_ADD);
        amqpAdmin.declareExchange(ex);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(ex));
        return queue;
    }

    @Bean
    public Queue mqRoleRemove() {
        Queue queue = new Queue("mqRoleRemove_" + Dew.Util.createShortUUID(), false,true,true);
        FanoutExchange ex = new FanoutExchange(Dew.Constant.MQ_AUTH_ROLE_REMOVE);
        amqpAdmin.declareExchange(ex);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(ex));
        return queue;
    }

}
