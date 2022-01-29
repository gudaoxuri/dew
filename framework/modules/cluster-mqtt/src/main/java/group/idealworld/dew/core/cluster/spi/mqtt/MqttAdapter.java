/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.core.cluster.spi.mqtt;

import group.idealworld.dew.core.cluster.Cluster;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
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
    private final MqttConfig mqttConfig;

    MqttAdapter(MqttConfig mqttConfig) throws MqttException {
        this.mqttConfig = mqttConfig;
        init();
    }

    private void init() throws MqttException {
        var clientId = mqttConfig.getClientId();
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = "Dew_Cluster_" + Cluster.instanceId;
        }
        MqttClientPersistence persistence;
        if ("memory".equalsIgnoreCase(mqttConfig.getPersistence())) {
            persistence = new MemoryPersistence();
        } else {
            persistence = new MqttDefaultFilePersistence();
        }
        client = new MqttClient(mqttConfig.getBroker(), clientId, persistence);
        var connOpts = new MqttConnectionOptions();
        if (mqttConfig.getUserName() != null) {
            connOpts.setUserName(mqttConfig.getUserName());
        }
        if (mqttConfig.getPassword() != null) {
            connOpts.setPassword(mqttConfig.getPassword().getBytes());
        }
        if (mqttConfig.getTimeoutSec() != null) {
            connOpts.setConnectionTimeout(mqttConfig.getTimeoutSec());
        }
        if (mqttConfig.getKeepAliveIntervalSec() != null) {
            connOpts.setKeepAliveInterval(mqttConfig.getKeepAliveIntervalSec());
        }
        connOpts.setAutomaticReconnect(true);
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
