package group.idealworld.dew.core.cluster.spi.rocket;

import group.idealworld.dew.core.cluster.AbsClusterMQ;
import group.idealworld.dew.core.cluster.dto.MessageWrap;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.header.SendMessageRequestHeader;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RocketClusterMQ extends AbsClusterMQ {

    private static SendBeforeFun sendBeforeFun = (topic, messageProperties) -> null;
    private static SendErrorFun sendErrorFun = (ex, beforeResult) -> {
    };
    private static SendFinishFun sendFinishFun = beforeResult -> {
    };
    private static ReceiveBeforeFun receiveBeforeFun = (topic, messageProperties) -> null;
    private static ReceiveErrorFun receiveErrorFun = (ex, beforeResult) -> {
    };
    private static ReceiveFinishFun receiveFinishFun = beforeResult -> {
    };

    private RocketAdapter rocketAdapter;

    private String nameServer;

    private String groupName;

    public RocketClusterMQ(RocketAdapter rocketAdapter, String nameServer, String groupName) {
        this.rocketAdapter = rocketAdapter;
        this.nameServer = nameServer;
        this.groupName = groupName;
    }

    /**
     * Sets send before fun.
     *
     * @param sendBeforeFun the send before fun
     */
    public static void setSendBeforeFun(SendBeforeFun sendBeforeFun) {
        RocketClusterMQ.sendBeforeFun = sendBeforeFun;
    }

    /**
     * Sets send error fun.
     *
     * @param sendErrorFun the send error fun
     */
    public static void setSendErrorFun(SendErrorFun sendErrorFun) {
        RocketClusterMQ.sendErrorFun = sendErrorFun;
    }

    /**
     * Sets send finish fun.
     *
     * @param sendFinishFun the send finish fun
     */
    public static void setSendFinishFun(SendFinishFun sendFinishFun) {
        RocketClusterMQ.sendFinishFun = sendFinishFun;
    }

    /**
     * Sets receive before fun.
     *
     * @param receiveBeforeFun the receive before fun
     */
    public static void setReceiveBeforeFun(ReceiveBeforeFun receiveBeforeFun) {
        RocketClusterMQ.receiveBeforeFun = receiveBeforeFun;
    }

    /**
     * Sets receive error fun.
     *
     * @param receiveErrorFun the receive error fun
     */
    public static void setReceiveErrorFun(ReceiveErrorFun receiveErrorFun) {
        RocketClusterMQ.receiveErrorFun = receiveErrorFun;
    }

    /**
     * Sets receive finish fun.
     *
     * @param receiveFinishFun the receive finish fun
     */
    public static void setReceiveFinishFun(ReceiveFinishFun receiveFinishFun) {
        RocketClusterMQ.receiveFinishFun = receiveFinishFun;
    }

    @Override
    protected boolean doPublish(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        RocketMQTemplate rocketMQTemplate = rocketAdapter.getRocketMQTemplate();
        Object funResult = null;
        try {
            SendMessageRequestHeader requestHeader = new SendMessageRequestHeader();
            Map<String, Object> sendHeader = getMQHeader(topic);
            header.ifPresent(sendHeader::putAll);
            Message<?> msg = MessageBuilder.withPayload(message).copyHeaders(sendHeader)
                    .build();
            funResult = sendBeforeFun.invoke(topic, sendHeader);
            if (confirm) {
                try {
                    SendResult result = rocketMQTemplate.syncSend(topic, msg);
                    return true;
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rocket publish error.", e);
                    sendErrorFun.invoke(e, funResult);
                    return false;
                }
            }
            rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
//                    System.out.println("发送成功" + count++);
                }

                @Override
                public void onException(Throwable throwable) {
                    logger.error("[MQ] Rocket publish error.", throwable);
                }
            });

        } catch (Exception e) {
            logger.error("[MQ] Rocket publish error.", e);
            sendErrorFun.invoke(e, funResult);
            return false;
        }
        return true;
    }

    @Override
    protected void doSubscribe(String topic, Consumer<MessageWrap> consumer) {
        DefaultMQPushConsumer mqConsumer = new DefaultMQPushConsumer(groupName);
        mqConsumer.setNamesrvAddr(nameServer);
        mqConsumer.setInstanceName(UUID.randomUUID().toString());

        try {
            mqConsumer.subscribe(topic, "*");
            mqConsumer.setMessageModel(MessageModel.BROADCASTING);
            mqConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            mqConsumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                try {
                    for (MessageExt messageExt : list) {
                        String messageBody = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                        Map<String, Object> headers = messageExt.getProperties()
                                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                        Map<String, Object> receiveHeader = setMQHeader(topic, headers);
                        consumer.accept(new MessageWrap(topic, Optional.of(receiveHeader), messageBody));
                    }
                } catch (IOException e) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            mqConsumer.start();
        } catch (MQClientException e) {

        }
    }

    @Override
    protected boolean doRequest(String address, String message, Optional<Map<String, Object>> header, boolean confirm) {
        RocketMQTemplate rocketMQTemplate = rocketAdapter.getRocketMQTemplate();
        try {
            SendMessageRequestHeader requestHeader = new SendMessageRequestHeader();
            Map<String, Object> sendHeader = getMQHeader(address);
            header.ifPresent(sendHeader::putAll);
            Message<?> msg = MessageBuilder.withPayload(message).copyHeaders(sendHeader)
                    .build();
            if (confirm) {
                try {
                    SendResult result = rocketMQTemplate.syncSend(address, msg);
                    return true;
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rocket publish error.", e);
                    return false;
                }
            }
            rocketMQTemplate.asyncSend(address, msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.info("send successful");
                }

                @Override
                public void onException(Throwable throwable) {
                    logger.info("send fail; {}", throwable.getMessage());
                }
            });

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void doResponse(String address, Consumer<MessageWrap> consumer) {
        DefaultMQPushConsumer mqConsumer = new DefaultMQPushConsumer(groupName);
        mqConsumer.setNamesrvAddr(nameServer);
        mqConsumer.setInstanceName(UUID.randomUUID().toString());

        try {
            mqConsumer.subscribe(address, "*");
            mqConsumer.setMessageModel(MessageModel.CLUSTERING);
            mqConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            mqConsumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                try {
                    for (MessageExt messageExt : list) {
                        String messageBody = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                        Map<String, String> map = messageExt.getProperties();
                        Map tempMap = map;
                        Map<String, Object> header = tempMap;
                        Map<String, Object> receiveHeader = setMQHeader(address, header);
                        consumer.accept(new MessageWrap(address, Optional.of(receiveHeader), messageBody));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            mqConsumer.start();
        } catch (MQClientException e) {

        }
    }

    @Override
    public boolean supportHeader() {
        return true;
    }
}
