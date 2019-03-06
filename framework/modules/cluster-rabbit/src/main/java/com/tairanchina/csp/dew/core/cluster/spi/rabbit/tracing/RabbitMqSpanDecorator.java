package com.tairanchina.csp.dew.core.cluster.spi.rabbit.tracing;

import com.rabbitmq.client.AMQP;
import io.opentracing.Span;
import io.opentracing.tag.Tags;

import java.util.LinkedHashMap;
import java.util.Map;

class RabbitMqSpanDecorator {

    static void onSend(AMQP.BasicProperties messageProperties, String exchange, String routingKey, Span span) {
        Tags.COMPONENT.set(span, RabbitMqTracingTags.RABBITMQ);
        RabbitMqTracingTags.EXCHANGE.set(span, exchange);
        RabbitMqTracingTags.MESSAGE_ID.set(span, messageProperties.getMessageId());
        RabbitMqTracingTags.ROUTING_KEY.set(span, routingKey);
    }

    static void onReceive(AMQP.BasicProperties messageProperties, String exchange, String routingKey, String queueName, Span span) {
        Tags.COMPONENT.set(span, RabbitMqTracingTags.RABBITMQ);
        RabbitMqTracingTags.EXCHANGE.set(span, exchange);
        RabbitMqTracingTags.MESSAGE_ID.set(span, messageProperties.getMessageId());
        RabbitMqTracingTags.ROUTING_KEY.set(span, routingKey);
        RabbitMqTracingTags.CONSUMER_QUEUE.set(span, queueName);
    }

    static void onError(Exception ex, Span span) {
        Map<String, Object> exceptionLogs = new LinkedHashMap<>(2);
        exceptionLogs.put("event", Tags.ERROR.getKey());
        exceptionLogs.put("error.object", ex);
        span.log(exceptionLogs);
        Tags.ERROR.set(span, true);
    }
}
