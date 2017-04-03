package com.ecfront.dew.wsgateway;


import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.MQInitiator;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GatewayMQInitiator extends MQInitiator {

    @PostConstruct
    public void init(Queue mqResourceAdd, Queue mqResourceRemove, Queue mqRoleAdd, Queue mqRoleRemove) {
        FanoutExchange exResourceAdd = new FanoutExchange(Dew.Constant.MQ_AUTH_RESOURCE_ADD);
        amqpAdmin.declareExchange(exResourceAdd);
        BindingBuilder.bind(mqResourceAdd).to(exResourceAdd);
        FanoutExchange exResourceRemove = new FanoutExchange(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE);
        amqpAdmin.declareExchange(exResourceRemove);
        BindingBuilder.bind(mqResourceRemove).to(exResourceRemove);
        FanoutExchange exRoleAdd = new FanoutExchange(Dew.Constant.MQ_AUTH_ROLE_ADD);
        amqpAdmin.declareExchange(exRoleAdd);
        BindingBuilder.bind(mqRoleAdd).to(exRoleAdd);
        FanoutExchange exRoleRemove = new FanoutExchange(Dew.Constant.MQ_AUTH_ROLE_REMOVE);
        amqpAdmin.declareExchange(exRoleRemove);
        BindingBuilder.bind(mqRoleRemove).to(exRoleRemove);
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_REFRESH, "");
    }

    @Bean
    public Queue mqResourceAdd() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue mqResourceRemove() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue mqRoleAdd() {
        return new AnonymousQueue();
    }

    @Bean
    public Queue mqRoleRemove() {
        return new AnonymousQueue();
    }

}
