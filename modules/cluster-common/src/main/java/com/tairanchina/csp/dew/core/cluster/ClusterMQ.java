package com.tairanchina.csp.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * MQ服务
 */
public interface ClusterMQ {

    Logger logger = LoggerFactory.getLogger(ClusterMQ.class);

    /**
     * MQ 发布订阅模式 之 发布
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @return 是否发布成功，此返回值仅在rabbit confirm 模式下才能保证严格准确！
     */
    boolean publish(String topic, String message);

    /**
     * MQ 发布订阅模式 之 订阅
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    void subscribe(String topic, Consumer<String> consumer);

    /**
     * MQ 请求响应模式 之 请求
     *
     * @param address 请求地址
     * @param message 消息内容
     * @return 是否请求成功
     */
    boolean request(String address, String message);

    /**
     * MQ 请求响应模式 之 响应
     * <p>
     * 非阻塞方式
     *
     * @param address  请求对应的地址
     * @param consumer 响应处理方法
     */
    void response(String address, Consumer<String> consumer);

    /**
     * MQ 请求响应模式 之 响应
     * <p>
     * 非阻塞方式
     *
     * @param address  请求对应的地址
     * @param consumer 响应处理方法
     */
    void responseAsyn(String address, int threadNum, Consumer<String> consumer, Consumer<Exception> failed);

    default Map<String, Object> getMQHeader(String name) {
        return Cluster.getMQHeader(name);
    }

    default void setMQHeader(String name, Map<String, Object> header) {
        Cluster.setMQHeader(name, header);
    }

}
