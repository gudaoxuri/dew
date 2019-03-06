package com.tairanchina.csp.dew.core.cluster.spi.rabbit.tracing;

import io.opentracing.propagation.TextMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class RabbitMqMessagePropertiesExtractAdapter implements TextMap {

  private final Map<String, String> map = new HashMap<>();

  RabbitMqMessagePropertiesExtractAdapter(Map<String, Object> headers) {
    headers.forEach(
        (key, value) -> {
          if (value == null) {
            return;
          }
          map.put(key, value.toString());
        });
  }

  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    return map.entrySet().iterator();
  }

  @Override
  public void put(String key, String value) {
    throw new UnsupportedOperationException("Should be used only with tracer#extract()");
  }
}
