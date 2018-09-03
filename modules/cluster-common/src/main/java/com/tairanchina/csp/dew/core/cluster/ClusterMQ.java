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
    default boolean publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        return doPublish(topic, message);
    }

    boolean doPublish(String topic, String message);

    /**
     * MQ 发布订阅模式 之 订阅
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    default void subscribe(String topic, Consumer<String> consumer) {
        logger.trace("[MQ] subscribe {}", topic);
        receiveMsg(topic, consumer, false);
    }

    void doSubscribe(String topic, Consumer<String> consumer);

    /**
     * MQ 请求响应模式 之 请求
     *
     * @param address 请求地址
     * @param message 消息内容
     * @return 是否请求成功
     */
    default boolean request(String address, String message) {
        logger.trace("[MQ] request {}:{}", address, message);
        return doRequest(address, message);
    }

    boolean doRequest(String address, String message);


    /**
     * MQ 请求响应模式 之 响应
     * <p>
     * 非阻塞方式
     *
     * @param address  请求对应的地址
     * @param consumer 响应处理方法
     */
    default void response(String address, Consumer<String> consumer) {
        logger.trace("[MQ] response {}", address);
        receiveMsg(address, consumer, true);
    }

    void doResponse(String address, Consumer<String> consumer);

    default void receiveMsg(String msgAddr, Consumer<String> consumer, boolean isResponse) {
        if (Cluster.haEnabled()) {
            boolean hasError = Cluster.getClusterHA().mq_findAllUnCommittedMsg(msgAddr).stream().anyMatch(haMsg -> {
                logger.trace("[MQ] receive by HA {}:{}", msgAddr, haMsg.getMsg());
                try {
                    consumer.accept(haMsg.getMsg());
                    Cluster.getClusterHA().mq_afterMsgAcked(haMsg.getMsgId());
                    return false;
                } catch (Exception e) {
                    logger.error("[MQ] receive by HA error.", e);
                    return true;
                }
            });
            if (hasError) {
                return;
            }
        }
        Consumer<String> fun = msg -> {
            logger.trace("[MQ] receive {}:{}", msgAddr, msg);
            try {
                if (Cluster.haEnabled()) {
                    String id = Cluster.getClusterHA().mq_afterPollMsg(msgAddr, msg);
                    consumer.accept(msg);
                    Cluster.getClusterHA().mq_afterMsgAcked(id);
                } else {
                    consumer.accept(msg);
                }
            } catch (Exception e) {
                logger.error("[MQ] receive error.", e);
            }
        };
        if (isResponse) {
            doResponse(msgAddr, fun);
        } else {
            doSubscribe(msgAddr, fun);
        }
    }

    default Map<String, Object> getMQHeader(String name) {
        return Cluster.getMQHeader(name);
    }

    default void setMQHeader(String name, Map<String, Object> header) {
        Cluster.setMQHeader(name, header);
    }

}
