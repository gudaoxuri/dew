package com.ecfront.dew.core;


import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MQInitiator {

    @Autowired
    protected AmqpAdmin amqpAdmin;

}
