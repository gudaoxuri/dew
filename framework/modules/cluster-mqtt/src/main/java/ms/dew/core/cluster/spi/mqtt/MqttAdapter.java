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

package ms.dew.core.cluster.spi.mqtt;

import ms.dew.core.cluster.Cluster;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mqtt adapter.
 *
 * @author gudaoxuri
 */
public class MqttAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MqttAdapter.class);

    private MqttClient client;
    private MqttConfig mqttConfig;

    MqttAdapter(MqttConfig mqttConfig) throws MqttException {
        this.mqttConfig = mqttConfig;
        init();
    }

    private void init() throws MqttException {
        String clientId = mqttConfig.getClientId();
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = "Dew_Cluster_" + Cluster.instanceId;
        }
        MqttClientPersistence persistence;
        switch (mqttConfig.getPersistence().toLowerCase()) {
            case "memory":
                persistence = new MemoryPersistence();
                break;
            default:
                persistence = new MqttDefaultFilePersistence();
        }
        client = new MqttClient(mqttConfig.getBroker(), clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        if (mqttConfig.getUserName() != null) {
            connOpts.setUserName(mqttConfig.getUserName());
        }
        if (mqttConfig.getPassword() != null) {
            connOpts.setPassword(mqttConfig.getPassword().toCharArray());
        }
        if (mqttConfig.getCleanSession() != null) {
            connOpts.setCleanSession(mqttConfig.getCleanSession());
        }
        if (mqttConfig.getTimeoutSec() != null) {
            connOpts.setConnectionTimeout(mqttConfig.getTimeoutSec());
        }
        if (mqttConfig.getKeepAliveIntervalSec() != null) {
            connOpts.setKeepAliveInterval(mqttConfig.getKeepAliveIntervalSec());
        }
        logger.info("[" + clientId + "] Connecting to broker: " + mqttConfig.getBroker());
        client.connect(connOpts);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }));
    }


    MqttClient getClient() {
        return client;
    }


}
