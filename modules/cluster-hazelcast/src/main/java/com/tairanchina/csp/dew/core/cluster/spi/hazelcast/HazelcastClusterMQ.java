package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.common.$;
import com.hazelcast.client.HazelcastClientNotActiveException;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import com.tairanchina.csp.dew.core.h2.H2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

public class HazelcastClusterMQ implements ClusterMQ {

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

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
    public void doResponse(String address, Consumer<String> consumer) {
            try {
                while (hazelcastAdapter.isActive()) {
                    String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                    String uuid = $.field.createUUID();
                    H2Utils.createJob(address, uuid, "RUNNING", message);
                    logger.trace("[MQ] response {}:{}", address, message);
                    consumer.accept(message);
                    H2Utils.deleteJob(uuid);
                }
            } catch (HazelcastClientNotActiveException e) {
                if (hazelcastAdapter.isActive()) {
                    logger.error("Hazelcast Response error.", e);
                }
            } catch (Exception e) {
                logger.error("Hazelcast Response error.", e);
            }
    }

}
