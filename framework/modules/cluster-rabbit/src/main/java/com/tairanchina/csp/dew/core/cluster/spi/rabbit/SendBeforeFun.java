package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.AMQP;

@FunctionalInterface
public interface SendBeforeFun {

    Object invoke(String exchange, String routingKey, AMQP.BasicProperties messageProperties);

}
