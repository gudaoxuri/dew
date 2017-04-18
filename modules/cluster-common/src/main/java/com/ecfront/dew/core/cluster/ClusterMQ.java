package com.ecfront.dew.core.cluster;

import java.util.function.Consumer;

public interface ClusterMQ {

    void publish(String topic, String message);

    void subscribe(String topic, Consumer<String> consumer);

    void request(String address, String message);

    void response(String address, Consumer<String> consumer);

}
