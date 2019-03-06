package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.AMQP;

@FunctionalInterface
public interface ReceiveBeforeFun {

    Object invoke(String exchange, String routingKey,String queueName,AMQP.BasicProperties messageProperties);

}
