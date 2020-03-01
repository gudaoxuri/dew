/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.core.cluster.spi.rabbit.tracing;

import io.opentracing.tag.StringTag;

/**
 * Rabbit mq tracing tags.
 *
 * @author gudaoxuri
 */
final class RabbitMqTracingTags {

    /**
     * The Rabbitmq.
     */
    static final StringTag RABBITMQ = new StringTag("rabbitmq");
    /**
     * The Message id.
     */
    static final StringTag MESSAGE_ID = new StringTag("messageid");
    /**
     * The Routing key.
     */
    static final StringTag ROUTING_KEY = new StringTag("routingkey");
    /**
     * The Consumer queue.
     */
    static final StringTag CONSUMER_QUEUE = new StringTag("consumerqueue");
    /**
     * The Exchange.
     */
    static final StringTag EXCHANGE = new StringTag("exchange");
    /**
     * The Span kind producer.
     */
    static final String SPAN_KIND_PRODUCER = RABBITMQ.getKey() + "-send";
    /**
     * The Span kind consumer.
     */
    static final String SPAN_KIND_CONSUMER = RABBITMQ.getKey() + "-receive";

}
