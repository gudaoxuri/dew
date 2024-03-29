package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.dto.MessageWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * MQ服务.
 *
 * @author gudaoxuri
 */
public abstract class AbsClusterMQ implements ClusterMQ {

    protected static Logger logger = LoggerFactory.getLogger(AbsClusterMQ.class);

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    @Override
    public boolean publish(String topic, String message, Map<String, Object> header, boolean confirm) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        return doPublish(topic, message, Optional.ofNullable(header), confirm);
    }

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    protected abstract boolean doPublish(String topic, String message, Optional<Map<String, Object>> header,
            boolean confirm);

    /**
     * MQ 发布订阅模式 之 订阅.
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    @Override
    public void subscribe(String topic, Consumer<MessageWrap> consumer) {
        logger.trace("[MQ] subscribe {}", topic);
        receiveMsg(topic, consumer, false);
    }

    /**
     * MQ 发布订阅模式 之 订阅.
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    protected abstract void doSubscribe(String topic, Consumer<MessageWrap> consumer);

    /**
     * MQ 请求响应模式 之 请求.
     *
     * @param address 请求地址
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    @Override
    public boolean request(String address, String message, Map<String, Object> header, boolean confirm) {
        logger.trace("[MQ] request {}:{}", address, message);
        return doRequest(address, message, Optional.ofNullable(header), confirm);
    }

    /**
     * MQ 请求响应模式 之 请求.
     *
     * @param address 请求地址
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    protected abstract boolean doRequest(String address, String message, Optional<Map<String, Object>> header,
            boolean confirm);

    /**
     * MQ 请求响应模式 之 响应.
     * <p>
     * 非阻塞方式
     *
     * @param address  请求对应的地址
     * @param consumer 响应处理方法
     */
    @Override
    public void response(String address, Consumer<MessageWrap> consumer) {
        logger.trace("[MQ] response {}", address);
        receiveMsg(address, consumer, true);
    }

    /**
     * MQ 请求响应模式 之 响应.
     * <p>
     * 非阻塞方式
     *
     * @param address  请求对应的地址
     * @param consumer 响应处理方法
     */
    protected abstract void doResponse(String address, Consumer<MessageWrap> consumer);

    private void receiveMsg(String msgAddr, Consumer<MessageWrap> consumer, boolean isResponse) {
        if (Cluster.haEnabled()) {
            boolean hasError = Cluster.getClusterHA().mqFindAllUnCommittedMsg(msgAddr).stream().anyMatch(haMsg -> {
                logger.trace("[MQ] receive by HA {}:{}", msgAddr, haMsg.getMsg());
                try {
                    consumer.accept(haMsg.getMsg());
                    Cluster.getClusterHA().mqAfterMsgAcked(haMsg.getMsgId());
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
        Consumer<MessageWrap> fun = msg -> {
            logger.trace("[MQ] receive {}:{}", msgAddr, msg);
            try {
                if (Cluster.haEnabled()) {
                    String id = Cluster.getClusterHA().mqAfterPollMsg(msgAddr, msg);
                    consumer.accept(msg);
                    Cluster.getClusterHA().mqAfterMsgAcked(id);
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
