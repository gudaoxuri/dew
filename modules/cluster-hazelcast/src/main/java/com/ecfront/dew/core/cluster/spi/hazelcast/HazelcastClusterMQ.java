package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClientNotActiveException;
import com.ecfront.dew.core.cluster.ClusterMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@ConditionalOnBean(HazelcastAdapter.class)
public class HazelcastClusterMQ implements ClusterMQ {

    @Autowired
    private HazelcastAdapter hazelcastAdapter;

    @Override
    public boolean publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
        return true;
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(message -> {
            try {
                String msg = (String) message.getMessageObject();
                logger.trace("[MQ] subscribe {}:{}", topic, msg);
                consumer.accept(msg);
            } catch (Exception e) {
                logger.error("Hazelcast Subscribe error.", e);
            }
        });

    }

    @Override
    public boolean request(String address, String message) {
        logger.trace("[MQ] request {}:{}", address, message);
        return hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        new Thread(() -> {
            try {
                while (hazelcastAdapter.isActive()) {
                    String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                    logger.trace("[MQ] response {}:{}", address, message);
                    consumer.accept(message);
                }
            } catch (HazelcastClientNotActiveException e) {
                if (hazelcastAdapter.isActive()) {
                    logger.error("Hazelcast Response error.", e);
                }
            } catch (Exception e) {
                logger.error("Hazelcast Response error.", e);
            }
        }).start();
    }

}
