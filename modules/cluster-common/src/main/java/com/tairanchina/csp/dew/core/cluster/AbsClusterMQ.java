package com.tairanchina.csp.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public abstract class AbsClusterMQ implements ClusterMQ {

    protected static Logger logger = LoggerFactory.getLogger(AbsClusterMQ.class);

    @Override
    public boolean publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        return doPublish(topic, message);
    }

    protected abstract boolean doPublish(String topic, String message);

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        logger.trace("[MQ] subscribe {}", topic);
        receiveMsg(topic, consumer, false);
    }

    protected abstract void doSubscribe(String topic, Consumer<String> consumer);

    @Override
    public boolean request(String address, String message) {
        logger.trace("[MQ] request {}:{}", address, message);
        return doRequest(address, message);
    }

    protected abstract boolean doRequest(String address, String message);


    @Override
    public void response(String address, Consumer<String> consumer) {
        logger.trace("[MQ] response {}", address);
        receiveMsg(address, consumer, true);
    }

    protected abstract void doResponse(String address, Consumer<String> consumer);

    private void receiveMsg(String msgAddr, Consumer<String> consumer, boolean isResponse) {
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
                throw new RuntimeException("[MQ] receive error:" + msg, e);
            }
        };
        if (isResponse) {
            doResponse(msgAddr, fun);
        } else {
            doSubscribe(msgAddr, fun);
        }
    }

}
