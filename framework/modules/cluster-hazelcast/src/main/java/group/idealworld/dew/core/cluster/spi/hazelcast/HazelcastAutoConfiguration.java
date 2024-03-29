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
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='hazelcast' "
        + "|| '${dew.cluster.mq}'=='hazelcast' "
        + "|| '${dew.cluster.lock}'=='hazelcast' "
        + "|| '${dew.cluster.map}'=='hazelcast' "
        + "|| '${dew.cluster.election}'=='hazelcast'}")
public class HazelcastAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastAutoConfiguration.class);

    private HazelcastConfig hazelcastConfig;

    /**
     * Instantiates a new Hazelcast auto configuration.
     *
     * @param hazelcastConfig the hazelcast config
     */
    public HazelcastAutoConfiguration(HazelcastConfig hazelcastConfig) {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
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
