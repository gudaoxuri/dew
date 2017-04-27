package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class HazelcastClusterMQ implements ClusterMQ {

    @Autowired
    private HazelcastAdapter hazelcastAdapter;

    @Override
    public void publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}",topic,message);
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(message -> {
            try {
                String msg=(String) message.getMessageObject();
                logger.trace("[MQ] subscribe {}:{}",topic,msg);
                consumer.accept(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void request(String address, String message) {
        logger.trace("[MQ] request {}:{}",address,message);
        hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        new Thread(() -> {
            try {
                while (true) {
                    String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                    logger.trace("[MQ] response {}:{}",address,message);
                    consumer.accept(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
