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

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Mqtt auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(MqttClient.class)
@ConditionalOnExpression("#{'${dew.cluster.mq}'=='mqtt'}")

public class MqttAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Mqtt adapter.
     *
     * @param mqttConfig the mqtt config
     * @return the mqtt adapter
     */
    @Bean
    public MqttAdapter mqttAdapter(MqttConfig mqttConfig) throws MqttException {
        return new MqttAdapter(mqttConfig);
    }

    /**
     * Mqtt cluster mq.
     *
     * @param mqttAdapter the mqtt adapter
     * @return the mqtt cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='mqtt'")
    public MqttClusterMQ mqttClusterMQ(MqttAdapter mqttAdapter) {
        return new MqttClusterMQ(mqttAdapter);
    }

}
