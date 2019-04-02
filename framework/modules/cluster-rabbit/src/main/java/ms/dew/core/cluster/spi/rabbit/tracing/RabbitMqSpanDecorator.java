/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.dew.core.cluster.spi.rabbit.tracing;

import com.rabbitmq.client.AMQP;
import io.opentracing.Span;
import io.opentracing.tag.Tags;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rabbit mq span decorator.
 *
 * @author gudaoxuri
 */
class RabbitMqSpanDecorator {

    /**
     * On send.
     *
     * @param messageProperties the message properties
     * @param exchange          the exchange
     * @param routingKey        the routing key
     * @param span              the span
     */
    static void onSend(AMQP.BasicProperties messageProperties, String exchange, String routingKey, Span span) {
        Tags.COMPONENT.set(span, RabbitMqTracingTags.RABBITMQ);
        RabbitMqTracingTags.EXCHANGE.set(span, exchange);
        RabbitMqTracingTags.MESSAGE_ID.set(span, messageProperties.getMessageId());
        RabbitMqTracingTags.ROUTING_KEY.set(span, routingKey);
    }

    /**
     * On receive.
     *
     * @param messageProperties the message properties
     * @param exchange          the exchange
     * @param routingKey        the routing key
     * @param queueName         the queue name
     * @param span              the span
     */
    static void onReceive(AMQP.BasicProperties messageProperties, String exchange, String routingKey, String queueName, Span span) {
        Tags.COMPONENT.set(span, RabbitMqTracingTags.RABBITMQ);
        RabbitMqTracingTags.EXCHANGE.set(span, exchange);
        RabbitMqTracingTags.MESSAGE_ID.set(span, messageProperties.getMessageId());
        RabbitMqTracingTags.ROUTING_KEY.set(span, routingKey);
        RabbitMqTracingTags.CONSUMER_QUEUE.set(span, queueName);
    }

    /**
     * On error.
     *
     * @param ex   the ex
     * @param span the span
     */
    static void onError(Exception ex, Span span) {
        Map<String, Object> exceptionLogs = new LinkedHashMap<>(2);
        exceptionLogs.put("event", Tags.ERROR.getKey());
        exceptionLogs.put("error.object", ex);
        span.log(exceptionLogs);
        Tags.ERROR.set(span, true);
    }
}
