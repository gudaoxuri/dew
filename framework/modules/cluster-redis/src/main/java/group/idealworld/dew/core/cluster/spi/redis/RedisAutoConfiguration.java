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

package group.idealworld.dew.core.cluster.spi.redis;

import io.lettuce.core.resource.ClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.MultiConnectionConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis auto configuration.
 *
 * @author gudaoxuri
 */
@EnableConfigurationProperties(MultiRedisConfig.class)
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='redis' " + "|| '${dew.cluster.mq}'=='redis' " + "|| '${dew.cluster.lock}'=='redis' " + "|| " +
        "'${dew.cluster.map}'=='redis' " + "|| '${dew.cluster.election}'=='redis'}")
public class RedisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisAutoConfiguration.class);

    @Autowired
    private MultiRedisConfig multiRedisConfig;

    @Value("${dew.cluster.config.election-period-sec:60}")
    private int electionPeriodSec;

    @Autowired
    private ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider;
    @Autowired
    private ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider;
    @Autowired
    private ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider;
    @Autowired
    private ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers;
    @Autowired
    private ClientResources clientResources;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

    private static final Map<String, RedisTemplate<String, String>> REDIS_TEMPLATES = new HashMap<>();

    /**
     * Init.
     *
     * @throws UnknownHostException the unknown host exception
     */
    @PostConstruct
    public void init() throws UnknownHostException {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        REDIS_TEMPLATES.put("", redisTemplate);
        if (!multiRedisConfig.getMulti().isEmpty()) {
            initMultiDS(multiRedisConfig.getMulti());
        }
    }

    private void initMultiDS(Map<String, RedisProperties> properties)  {
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        for (Map.Entry<String, RedisProperties> prop : properties.entrySet()) {
            LettuceConnectionFactory redisConnectionFactory = new MultiConnectionConfiguration(prop.getValue(), standaloneConfigurationProvider,
                    sentinelConfigurationProvider, clusterConfigurationProvider).redisConnectionFactory(builderCustomizers, clientResources);
            redisConnectionFactory.afterPropertiesSet();
            RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.setKeySerializer(RedisSerializer.string());
            redisTemplate.setValueSerializer(RedisSerializer.string());
            redisTemplate.setHashKeySerializer(RedisSerializer.string());
            redisTemplate.setHashValueSerializer(RedisSerializer.string());
            beanFactory.registerSingleton(prop.getKey() + "RedisTemplate", redisTemplate);
            redisTemplate = (RedisTemplate) beanFactory.getBean(prop.getKey() + "RedisTemplate");
            redisTemplate.afterPropertiesSet();
            REDIS_TEMPLATES.put(prop.getKey(), redisTemplate);
        }
    }

    /**
     * Redis cluster cache redis cluster cache.
     *
     * @return the redis cluster cache
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.cache}'=='redis'")
    public RedisClusterCacheWrap redisClusterCache() {
        return new RedisClusterCacheWrap(REDIS_TEMPLATES);
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
