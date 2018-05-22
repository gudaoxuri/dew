package com.tairanchina.csp.dew.core.cluster.spi.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * desription:
 * Created by ding on 2017/11/9.
 */
public class KafkaAdapter {

    private KafkaProducer<String, String> kafkaProducer;

    private Properties consumerProperties;

    private KafkaProperties kafkaProperties;

    public KafkaAdapter(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @PostConstruct
    public void init() throws IllegalAccessException {
        // 生产者（发布）
        this.kafkaProducer = new KafkaProducer<>(getProperties(kafkaProperties.getProducer(), KafkaProperties.Producer.class));
        // 消费者（订阅）
        this.consumerProperties = getProperties(kafkaProperties.getConsumer(), KafkaProperties.Consumer.class);
    }

    KafkaProducer<String, String> getKafkaProducer() {
        return kafkaProducer;
    }

    KafkaConsumer<String, String> getKafkaConsumer(boolean isSubscibe) {

        if (isSubscibe) {
            Properties properties = new Properties();
            consumerProperties.forEach((k, v) -> {
                if (k.equals("group.id")) {
                    properties.put(k, v + "_" + System.currentTimeMillis() + "_" + Math.random());
                } else {
                    properties.put(k, v);
                }
            });
            return new KafkaConsumer<>(properties);
        }
        return new KafkaConsumer<>(consumerProperties);
    }

    private Properties getProperties(Object obj, Class classType) throws IllegalAccessException {
        Properties properties = new Properties();
        Field[] fields = classType.getDeclaredFields();
        Field.setAccessible(fields, true);
        for (Field f : fields) {
            if (f.get(obj) != null) {
                properties.put(fileNameToKey(f.getName()), f.get(obj));
            }
        }
        return properties;
    }

    private String fileNameToKey(Object obj) {
        String param = String.valueOf(obj);
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(".");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
