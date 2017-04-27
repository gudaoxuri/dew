package com.ecfront.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public interface ClusterMQ {

    Logger logger = LoggerFactory.getLogger(ClusterMQ.class);

    void publish(String topic, String message);

    void subscribe(String topic, Consumer<String> consumer);

    void request(String address, String message);

    void response(String address, Consumer<String> consumer);

}
