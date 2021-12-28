/*
 * Copyright 2020. the original author or authors.
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

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

/**
 * rocket auto configuration.
 *
 * @author nipeixuan
 */
@Configuration
@ConditionalOnClass(RocketMQTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.mq}'=='rocket'}")
public class RocketAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RocketAutoConfiguration.class);

    @Value("${rocketmq.producer.group}")
    String groupName;

    @Value("${rocketmq.name-server}")
    String nameServer;


    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Rabbit adapter.
     *
     * @param rocketMQTemplate the rocket template
     * @return the rocket adapter
     */
    @Bean
    public RocketAdapter rocketAdapter(RocketMQTemplate rocketMQTemplate) {
        return new RocketAdapter(rocketMQTemplate);
    }

    /**
     * Rocket cluster mq.
     *
     * @param rocketAdapter the rocket adapter
     * @return the rocket cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='rocket'")
    public RocketClusterMQ rocketClusterMQ(RocketAdapter rocketAdapter) {
        return new RocketClusterMQ(rocketAdapter, nameServer, groupName);
    }

}
