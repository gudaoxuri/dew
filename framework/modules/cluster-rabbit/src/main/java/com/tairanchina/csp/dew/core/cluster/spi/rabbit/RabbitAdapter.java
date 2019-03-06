package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitAdapter {

    private RabbitTemplate rabbitTemplate;

    public RabbitAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    Connection getConnection() {
        return rabbitTemplate.getConnectionFactory().createConnection();
    }

}
