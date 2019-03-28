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

package ms.dew.core.cluster.spi.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * The type Redis auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='redis' "
        + "|| '${dew.cluster.mq}'=='redis' "
        + "|| '${dew.cluster.lock}'=='redis' "
        + "|| '${dew.cluster.map}'=='redis' "
        + "|| '${dew.cluster.election}'=='redis'}")
public class RedisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisAutoConfiguration.class);

    @Value("${dew.cluster.config.election-period-sec:60}")
    private int electionPeriodSec;

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Redis cluster cache redis cluster cache.
     *
     * @param redisTemplate the redis template
     * @return the redis cluster cache
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.cache}'=='redis'")
    public RedisClusterCache redisClusterCache(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterCache(redisTemplate);
    }

    /**
     * Redis cluster lock redis cluster lock wrap.
     *
     * @param redisTemplate the redis template
     * @return the redis cluster lock wrap
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.lock}'=='redis'")
    public RedisClusterLockWrap redisClusterLock(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterLockWrap(redisTemplate);
    }

    /**
     * Redis cluster map redis cluster map wrap.
     *
     * @param redisTemplate the redis template
     * @return the redis cluster map wrap
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.map}'=='redis'")
    public RedisClusterMapWrap redisClusterMap(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterMapWrap(redisTemplate);
    }

    /**
     * Redis cluster mq redis cluster mq.
     *
     * @param redisTemplate the redis template
     * @return the redis cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='redis'")
    public RedisClusterMQ redisClusterMQ(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterMQ(redisTemplate);
    }

    /**
     * Redis cluster election redis cluster election wrap.
     *
     * @param redisTemplate the redis template
     * @return the redis cluster election wrap
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.election}'=='redis'")
    public RedisClusterElectionWrap redisClusterElection(RedisTemplate<String, String> redisTemplate) {
        return new RedisClusterElectionWrap(redisTemplate, electionPeriodSec);
    }

}
