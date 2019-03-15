/*
 * Copyright 2019. the original author or authors.
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

package com.tairanchina.csp.dew.core.cluster;

import java.util.Map;
import java.util.function.Consumer;

/**
 * MQ服务
 */
public interface ClusterMQ {


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


    default Map<String, Object> getMQHeader(String name) {
        return Cluster.getMQHeader(name);
    }

    default void setMQHeader(String name, Map<String, Object> header) {
        Cluster.setMQHeader(name, header);
    }

}
