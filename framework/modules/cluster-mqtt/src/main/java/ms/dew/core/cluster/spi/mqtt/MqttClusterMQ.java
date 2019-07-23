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

package ms.dew.core.cluster.spi.mqtt;

import ms.dew.core.cluster.AbsClusterMQ;
import ms.dew.core.cluster.dto.MessageWrap;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * MQ服务 Mqtt 实现.
 *
 * @author gudaoxuri
 */
public class MqttClusterMQ extends AbsClusterMQ {

    private MqttAdapter mqttAdapter;

    MqttClusterMQ(MqttAdapter mqttAdapter) {
        this.mqttAdapter = mqttAdapter;
    }

    @Override
    protected boolean doPublish(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            if (confirm) {
                mqttMessage.setQos(1);
            } else {
                mqttMessage.setQos(0);
            }
            mqttAdapter.getClient().publish(topic, mqttMessage);
            return true;
        } catch (MqttException e) {
            logger.error("[MQ] Mqtt publish error.", e);
            return false;
        }
    }

    @Override
    protected void doSubscribe(String topic, Consumer<MessageWrap> consumer) {
        try {
            mqttAdapter.getClient().subscribe(topic,
                    (t, mqttMessage) ->
                            consumer.accept(new MessageWrap(t, new String(mqttMessage.getPayload(), StandardCharsets.UTF_8))));
        } catch (MqttException e) {
            logger.error("[MQ] Mqtt subscribe error.", e);
        }
    }

    @Override
    protected boolean doRequest(String address, String message, Optional<Map<String, Object>> header, boolean confirm) {
        throw new UnsupportedOperationException("MQTT doesn't support point-to-point mode");
    }

    @Override
    protected void doResponse(String address, Consumer<MessageWrap> consumer) {
        throw new UnsupportedOperationException("MQTT doesn't support point-to-point mode");
    }

    @Override
    public boolean supportHeader() {
        return false;
    }
}
