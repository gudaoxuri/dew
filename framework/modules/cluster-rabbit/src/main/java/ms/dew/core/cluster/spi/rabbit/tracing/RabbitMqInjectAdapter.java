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
import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;

/**
 * The type Rabbit mq inject adapter.
 *
 * @author gudaoxuri
 */
class RabbitMqInjectAdapter implements TextMap {

    private final AMQP.BasicProperties messageProperties;

    /**
     * Instantiates a new Rabbit mq inject adapter.
     *
     * @param messageProperties the message properties
     */
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
