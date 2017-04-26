package com.ecfront.dew.cluster.spi.ignite;

import com.ecfront.dew.core.cluster.ClusterMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class IgniteClusterMQ implements ClusterMQ {

    @Autowired
    private IgniteAdapter igniteAdapter;

    @Override
    public void publish(String topic, String message) {
        igniteAdapter.getIgnite().message(igniteAdapter.getIgnite().cluster().forRemotes()).send(topic, message);
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        igniteAdapter.getIgnite().message(igniteAdapter.getIgnite().cluster().forRemotes())
                .remoteListen(topic, (nodeId, message) -> {
                    try {
                        consumer.accept((String) message);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                });
    }

    @Override
    public void request(String address, String message) {
        igniteAdapter.getIgnite().queue(address, 0, null).add(message);
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        new Thread(() -> {
            try {
                while (true) {
                    String message = (String) igniteAdapter.getIgnite().queue(address, 0, null).take();
                    consumer.accept(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
