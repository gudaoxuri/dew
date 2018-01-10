package com.ecfront.dew.jdbc.sharding;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.yaml.sharding.YamlShardingRuleConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sharding rule configuration properties.
 *
 * @author caohao
 */
@ConditionalOnClass(ShardingDataSourceFactory.class)
@ConfigurationProperties(prefix = "sharding.jdbc.config.sharding")
public class ShardingRuleConfigurationProperties extends YamlShardingRuleConfiguration {
}
