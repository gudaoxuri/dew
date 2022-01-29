/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.dto.MessageWrap;

import java.util.Map;
import java.util.function.Consumer;

/**
 * MQ服务.
 *
 * @author gudaoxuri
 */
public interface ClusterMQ {

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功 ，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    boolean publish(String topic, String message, Map<String, Object> header, boolean confirm);

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     */
    default void publish(String topic, String message, Map<String, Object> header) {
        publish(topic, message, header, false);
    }

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     */
    default void publish(String topic, String message) {
        publish(topic, message, null);
    }


    /**
     * MQ 发布订阅模式 之 订阅.
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    void subscribe(String topic, Consumer<MessageWrap> consumer);

    /**
     * MQ 请求响应模式 之 请求.
     *
     * @param address 请求地址
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功 ，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    boolean request(String address, String message, Map<String, Object> header, boolean confirm);

    /**
     * MQ 请求响应模式 之 请求.
     *
     * @param address 请求地址
     * @param message 消息内容
     * @param header  消息头
     */
    default void request(String address, String message, Map<String, Object> header) {
        request(address, message, header, false);
    }


    /**
     * MQ 请求响应模式 之 请求.
     *
     * @param address 请求地址
     * @param message 消息内容
     */
    default void request(String address, String message) {
        request(address, message, null);
    }


    /**
     * MQ 请求响应模式 之 响应.
     * <p>
     * 非阻塞方式
     *
     * @param address  请求对应的地址
     * @param consumer 响应处理方法
     */
    void response(String address, Consumer<MessageWrap> consumer);

    /**
     * Gets mq header.
     *
     * @param name the name
     * @return the mq header
     */
    default Map<String, Object> getMQHeader(String name) {
        return Cluster.getMQHeader(name);
    }

    /**
     * Sets mq header.
     *
     * @param name   the name
     * @param header the header
     * @return the mq header
     */
    default Map<String, Object> setMQHeader(String name, Map<String, Object> header) {
        return Cluster.setMQHeader(name, header);
    }

    /**
     * Is header support.
     *
     * @return the result
     */
    boolean supportHeader();
}
