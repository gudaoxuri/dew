package org.springframework.boot.autoconfigure.data.redis;

import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * 多连接实例配置.
 *
 * @author gudaoxuri
 */
public class MultiConnectionConfiguration extends LettuceConnectionConfiguration {

    /**
     * 初始化.
     *
     * @param properties                      redis配置
     * @param standaloneConfigurationProvider standalone配置
     * @param sentinelConfigurationProvider   sentinel配置
     * @param clusterConfigurationProvider    cluster配置
     * @param redisConnectionDetails          redis连接信息
     */
    public MultiConnectionConfiguration(RedisProperties properties,
                                        ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
                                        ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                        ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                        RedisConnectionDetails redisConnectionDetails) {
        super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider,
                redisConnectionDetails);
    }

    @Override
    public LettuceConnectionFactory redisConnectionFactory(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
            ObjectProvider<LettuceClientOptionsBuilderCustomizer> customizers,
            ClientResources clientResources) {
        return super.redisConnectionFactory(builderCustomizers, customizers, clientResources);
    }

}
