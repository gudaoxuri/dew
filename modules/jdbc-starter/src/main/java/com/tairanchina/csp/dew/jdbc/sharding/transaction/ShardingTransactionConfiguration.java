package com.tairanchina.csp.dew.jdbc.sharding.transaction;

import com.tairanchina.csp.dew.jdbc.DewDS;
import com.tairanchina.csp.dew.jdbc.sharding.MasterSlaveRuleConfigurationProperties;
import com.tairanchina.csp.dew.jdbc.sharding.ShardingEnvironmentAware;
import com.tairanchina.csp.dew.jdbc.sharding.ShardingRuleConfigurationProperties;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.transaction.api.SoftTransactionManager;
import io.shardingjdbc.transaction.api.config.SoftTransactionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * desription:
 * Created by ding on 2017/12/13.
 */
@Configuration
@ConditionalOnExpression("'${sharding.enabled}'=='true'")
@EnableConfigurationProperties({ShardingRuleConfigurationProperties.class, MasterSlaveRuleConfigurationProperties.class})
public class ShardingTransactionConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ShardingTransactionConfiguration.class);

    @Value("${sharding.transaction.name:transaction}")
    private String transaction;

    private ShardingRuleConfigurationProperties shardingRuleConfigurationProperties;

    private MasterSlaveRuleConfigurationProperties masterSlaveRuleConfigurationProperties;

    public ShardingTransactionConfiguration(ShardingRuleConfigurationProperties shardingRuleConfigurationProperties, MasterSlaveRuleConfigurationProperties masterSlaveRuleConfigurationProperties) {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        this.shardingRuleConfigurationProperties = shardingRuleConfigurationProperties;
        this.masterSlaveRuleConfigurationProperties = masterSlaveRuleConfigurationProperties;
    }

    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public SoftTransactionManager softTransactionManager(ApplicationContext applicationContext, ShardingEnvironmentAware shardingEnvironmentAware) throws Exception {
        SoftTransactionConfiguration softTransactionConfiguration = new SoftTransactionConfiguration(shardingEnvironmentAware.dataSource());
        softTransactionConfiguration.setTransactionLogDataSource(((DewDS) applicationContext.getBean(transaction + "DS")).jdbc().getDataSource());
        SoftTransactionManager softTransactionManager = new SoftTransactionManager(softTransactionConfiguration);
        try {
            softTransactionManager.init();
        } catch (SQLException e) {
            logger.error("Dew error : softTransactionManager init failed ");
        }
        return softTransactionManager;
    }

    @Bean
    @ConditionalOnClass(ShardingDataSourceFactory.class)
    public ShardingEnvironmentAware shardingEnvironmentAware(){
        return new ShardingEnvironmentAware(shardingRuleConfigurationProperties,masterSlaveRuleConfigurationProperties);
    }


}