package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClientNotActiveException;
import com.tairanchina.csp.dew.core.cluster.AbsClusterMQ;

import java.util.function.Consumer;

public class HazelcastClusterMQ extends AbsClusterMQ {

    private HazelcastAdapter hazelcastAdapter;

    HazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    protected boolean doPublish(String topic, String message) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
        return true;
    }

    @Override
    protected void doSubscribe(String topic, Consumer<String> consumer) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(message ->
                consumer.accept((String) message.getMessageObject()));
    }

    @Override
    protected boolean doRequest(String address, String message) {
        return hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    protected void doResponse(String address, Consumer<String> consumer) {
        try {
            while (hazelcastAdapter.isActive()) {
                String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                consumer.accept(message);
            }
        } catch (HazelcastClientNotActiveException e) {
            if (hazelcastAdapter.isActive()) {
                logger.error("Hazelcast Response error.", e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Hazelcast Response error.", e);
        }
    }

}
