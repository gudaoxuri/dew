package com.ecfront.dew.jdbc.sharding;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.constant.ShardingPropertiesConstant;
import io.shardingjdbc.core.exception.ShardingJdbcException;
import io.shardingjdbc.core.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
@ConditionalOnClass(ShardingDataSourceFactory.class)
@EnableConfigurationProperties({ShardingRuleConfigurationProperties.class, MasterSlaveRuleConfigurationProperties.class})
public class ShardingConfiguration implements EnvironmentAware {

    @Autowired
    private ShardingRuleConfigurationProperties shardingProperties;

    @Autowired
    private MasterSlaveRuleConfigurationProperties masterSlaveProperties;

    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    private Set<String> jdbcUrls = new HashSet<>();

    private final Properties props = new Properties();

    public DataSource dataSource() throws SQLException {
        return null == masterSlaveProperties.getMasterDataSourceName() ? ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingProperties.getShardingRuleConfiguration(), props)
                : MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveProperties.getMasterSlaveRuleConfiguration());
    }

    @Override
    public void setEnvironment(final Environment environment) {
        setDataSourceMap(environment);
        setShardingProperties(environment);
    }

    private void setDataSourceMap(final Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "sharding.jdbc.datasource.");
        String dataSources = propertyResolver.getProperty("names");
        for (String each : dataSources.split(",")) {
            try {
                Map<String, Object> dataSourceProps = propertyResolver.getSubProperties(each + ".");
                Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
                DataSource dataSource = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
                dataSourceMap.put(each, dataSource);
                jdbcUrls.add(dataSourceProps.get("url").toString());
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingJdbcException("Can't find datasource type!", ex);
            }
        }
    }

    private void setShardingProperties(final Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "sharding.jdbc.config.sharding.props.");
        String showSQL = propertyResolver.getProperty(ShardingPropertiesConstant.SQL_SHOW.getKey());
        if (!Strings.isNullOrEmpty(showSQL)) {
            props.setProperty(ShardingPropertiesConstant.SQL_SHOW.getKey(), showSQL);
        }
        String executorSize = propertyResolver.getProperty(ShardingPropertiesConstant.EXECUTOR_SIZE.getKey());
        if (!Strings.isNullOrEmpty(executorSize)) {
            props.setProperty(ShardingPropertiesConstant.EXECUTOR_SIZE.getKey(), executorSize);
        }
    }

    public Set<String> getJdbcUrls() {
        return jdbcUrls;
    }

    public void setJdbcUrls(Set<String> jdbcUrls) {
        this.jdbcUrls = jdbcUrls;
    }
}
