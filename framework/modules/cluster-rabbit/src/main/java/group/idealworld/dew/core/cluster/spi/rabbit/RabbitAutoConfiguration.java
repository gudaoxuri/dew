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

package group.idealworld.dew.core.cluster.spi.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Rabbit auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.mq}'=='rabbit'}")
public class RabbitAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitAutoConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Rabbit adapter.
     *
     * @param rabbitTemplate the rabbit template
     * @return the rabbit adapter
     */
    @Bean
    public RabbitAdapter rabbitAdapter(RabbitTemplate rabbitTemplate) {
        return new RabbitAdapter(rabbitTemplate);
    }

    /**
     * Rabbit cluster mq.
     *
     * @param rabbitAdapter the rabbit adapter
     * @return the rabbit cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='rabbit'")
    public RabbitClusterMQ rabbitClusterMQ(RabbitAdapter rabbitAdapter) {
        return new RabbitClusterMQ(rabbitAdapter);
    }

}
