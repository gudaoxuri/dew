package com.tairanchina.csp.dew.core.cluster.spi.rabbit.tracing;

import io.opentracing.tag.StringTag;

final class RabbitMqTracingTags {

  static final StringTag RABBITMQ = new StringTag("rabbitmq");
  static final StringTag MESSAGE_ID = new StringTag("messageid");
  static final StringTag ROUTING_KEY = new StringTag("routingkey");
  static final StringTag CONSUMER_QUEUE = new StringTag("consumerqueue");
  static final StringTag EXCHANGE = new StringTag("exchange");
  static final String SPAN_KIND_PRODUCER = RABBITMQ.getKey() + "-send";
  static final String SPAN_KIND_CONSUMER = RABBITMQ.getKey() + "-receive";

}
