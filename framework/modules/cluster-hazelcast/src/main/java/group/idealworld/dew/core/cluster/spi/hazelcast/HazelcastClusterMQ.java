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

package group.idealworld.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.common.exception.RTUnsupportedEncodingException;
import com.hazelcast.client.HazelcastClientNotActiveException;
import group.idealworld.dew.core.cluster.AbsClusterMQ;
import group.idealworld.dew.core.cluster.dto.MessageWrap;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * MQ服务 Hazelcast 实现.
 *
 * @author gudaoxuri
 */
public class HazelcastClusterMQ extends AbsClusterMQ {

    private HazelcastAdapter hazelcastAdapter;

    /**
     * Instantiates a new Hazelcast cluster mq.
     *
     * @param hazelcastAdapter the hazelcast adapter
     */
    HazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    protected boolean doPublish(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        if (confirm) {
            throw new RTUnsupportedEncodingException("Hazelcast doesn't support confirm mode");
        }
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
        return true;
    }

    @Override
    protected void doSubscribe(String topic, Consumer<MessageWrap> consumer) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(message ->
                consumer.accept(new MessageWrap(topic, (String) message.getMessageObject())));
    }

    @Override
    protected boolean doRequest(String address, String message, Optional<Map<String, Object>> header, boolean confirm) {
        if (confirm) {
            throw new RTUnsupportedEncodingException("Hazelcast doesn't support confirm mode");
        }
        return hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    protected void doResponse(String address, Consumer<MessageWrap> consumer) {
        try {
            while (hazelcastAdapter.isActive()) {
                String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                consumer.accept(new MessageWrap(address, message));
            }
        } catch (HazelcastClientNotActiveException e) {
            if (hazelcastAdapter.isActive()) {
                logger.error("Hazelcast Response error.", e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Hazelcast Response error.", e);
        }
    }

    @Override
    public boolean supportHeader() {
        return false;
    }
}
