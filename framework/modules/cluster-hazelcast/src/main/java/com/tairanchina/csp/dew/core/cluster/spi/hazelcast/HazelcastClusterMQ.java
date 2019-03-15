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

package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.hazelcast.client.HazelcastClientNotActiveException;
import com.tairanchina.csp.dew.core.cluster.AbsClusterMQ;

import java.util.function.Consumer;

public class HazelcastClusterMQ extends AbsClusterMQ {

    private HazelcastAdapter hazelcastAdapter;

    HazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    protected boolean doPublish(String topic, String message) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).publish(message);
        return true;
    }

    @Override
    protected void doSubscribe(String topic, Consumer<String> consumer) {
        hazelcastAdapter.getHazelcastInstance().getTopic(topic).addMessageListener(message ->
                consumer.accept((String) message.getMessageObject()));
    }

    @Override
    protected boolean doRequest(String address, String message) {
        return hazelcastAdapter.getHazelcastInstance().getQueue(address).add(message);
    }

    @Override
    protected void doResponse(String address, Consumer<String> consumer) {
        try {
            while (hazelcastAdapter.isActive()) {
                String message = (String) hazelcastAdapter.getHazelcastInstance().getQueue(address).take();
                consumer.accept(message);
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

}
