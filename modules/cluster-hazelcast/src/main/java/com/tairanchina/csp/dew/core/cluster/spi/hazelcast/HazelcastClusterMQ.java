package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClientNotActiveException;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;

import java.util.function.Consumer;

public class HazelcastClusterMQ implements ClusterMQ {

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public boolean doPublish(String topic, String message) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
        return true;
    }

    @Override
    public void doSubscribe(String topic, Consumer<String> consumer) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(message ->
                consumer.accept((String) message.getMessageObject()));
    }

    @Override
    public boolean doRequest(String address, String message) {
        return hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    public void doResponse(String address, Consumer<String> consumer) {
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
