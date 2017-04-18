package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterMQ;
import com.ecfront.dew.core.cluster.MessageProcessFun;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HazelcastClusterMQ implements ClusterMQ {

    @Autowired
    private HazelcastAdapter hazelcastAdapter;

    @Override
    public void publish(String topic, String message) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
    }

    @Override
    public void subscribe(String topic, MessageProcessFun<String> messageProcessFun) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(new MessageListener<Object>() {
            @Override
            public void onMessage(Message<Object> message) {
                try {
                    messageProcessFun.received((String) message.getMessageObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void request(String address, String message) {
        hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    public void response(String address, MessageProcessFun<String> messageProcessFun) {
        new Thread(() -> {
            try {
                String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                messageProcessFun.received(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
