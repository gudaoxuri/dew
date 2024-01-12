package group.idealworld.dew.core.cluster.spi.mqtt;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Mqtt auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(MqttClient.class)
@ConditionalOnExpression("#{'${dew.cluster.mq}'=='mqtt'}")

public class MqttAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void initConf() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
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
