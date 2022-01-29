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

package group.idealworld.dew.core.cluster.spi.rocket;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * Rocket adapter.
 *
 * @author nipeixuan
 */
public class RocketAdapter {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * Instantiates a new Rabbit adapter.
     *
     * @param rocketMQTemplate the rabbit template
     */
    public RocketAdapter(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    DefaultMQProducer getProducer(){
        return rocketMQTemplate.getProducer();
    }

    RocketMQTemplate getRocketMQTemplate(){
        return this.rocketMQTemplate;
    }





}
