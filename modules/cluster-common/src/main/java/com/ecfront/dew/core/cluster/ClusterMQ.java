package com.ecfront.dew.core.cluster;

public interface ClusterMQ {

    void publish(String topic, String message);

    void subscribe(String topic, MessageProcessFun<String> messageProcessFun);

    void request(String address, String message);

    void response(String address, MessageProcessFun<String> messageProcessFun);

}
