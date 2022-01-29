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

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hazelcast auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(HazelcastInstance.class)
@EnableConfigurationProperties(HazelcastConfig.class)
@ConditionalOnExpression(
        "#{'${dew.cluster.cache}'=='hazelcast' "
                + "|| '${dew.cluster.mq}'=='hazelcast' "
                + "|| '${dew.cluster.lock}'=='hazelcast' "
                + "|| '${dew.cluster.map}'=='hazelcast' "
                + "|| '${dew.cluster.election}'=='hazelcast'}")
public class HazelcastAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastAutoConfiguration.class);

    private HazelcastConfig hazelcastConfig;

    /**
     * Instantiates a new Hazelcast auto configuration.
     *
     * @param hazelcastConfig the hazelcast config
     */
    public HazelcastAutoConfiguration(HazelcastConfig hazelcastConfig) {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        this.hazelcastConfig = hazelcastConfig;
    }

    /**
     * Hazelcast adapter hazelcast adapter.
     *
     * @return the hazelcast adapter
     */
    @Bean
    public HazelcastAdapter hazelcastAdapter() {
        return new HazelcastAdapter(hazelcastConfig);
    }

    /**
     * Hazelcast cluster lock hazelcast cluster lock wrap.
     *
     * @param hazelcastAdapter the hazelcast adapter
     * @return the hazelcast cluster lock wrap
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.lock}'=='hazelcast'")
    public HazelcastClusterLockWrap hazelcastClusterLock(HazelcastAdapter hazelcastAdapter) {
        return new HazelcastClusterLockWrap(hazelcastAdapter);
    }

    /**
     * Hazelcast cluster map hazelcast cluster map wrap.
     *
     * @param hazelcastAdapter the hazelcast adapter
     * @return the hazelcast cluster map wrap
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.map}'=='hazelcast'")
    public HazelcastClusterMapWrap hazelcastClusterMap(HazelcastAdapter hazelcastAdapter) {
        return new HazelcastClusterMapWrap(hazelcastAdapter);
    }

    /**
     * Hazelcast cluster mq hazelcast cluster mq.
     *
     * @param hazelcastAdapter the hazelcast adapter
     * @return the hazelcast cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='hazelcast'")
    public HazelcastClusterMQ hazelcastClusterMQ(HazelcastAdapter hazelcastAdapter) {
        return new HazelcastClusterMQ(hazelcastAdapter);
    }
}
