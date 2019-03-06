package com.tairanchina.csp.dew.core.cluster.spi.rabbit.tracing;

import com.rabbitmq.client.AMQP;
import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;

class RabbitMqInjectAdapter implements TextMap {

    private final AMQP.BasicProperties messageProperties;

    RabbitMqInjectAdapter(AMQP.BasicProperties messageProperties) {
        this.messageProperties = messageProperties;
    }

    @Override
    public void put(String key, String value) {
        messageProperties.getHeaders().put(key, value);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("iterator should never be used with Tracer.inject()");
    }
}
