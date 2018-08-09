package com.tairanchina.csp.dew.jdbc.mybatis;

import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.spring.boot.starter.ConfigurationCustomizer;
import com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusProperties;
import com.baomidou.mybatisplus.spring.boot.starter.SpringBootVFS;
import com.tairanchina.csp.dew.core.loading.DewLoadImmediately;
import com.tairanchina.csp.dew.jdbc.DewDSAutoConfiguration;
import com.tairanchina.csp.dew.jdbc.config.DewMultiDSConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Set;


@org.springframework.context.annotation.Configuration
@AutoConfigureAfter(DewDSAutoConfiguration.class)
@ConditionalOnClass({SqlSessionFactory.class, MybatisSqlSessionFactoryBean.class})
@EnableConfigurationProperties({MybatisPlusProperties.class,DewMultiDSConfig.class})
@DewLoadImmediately
public class MybatisStarterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisStarterConfiguration.class);
    private final MybatisPlusProperties properties;
    private final Interceptor[] interceptors;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;

    @Autowired
    private DewDSAutoConfiguration dewDSAutoConfiguration;

    @Autowired
    private DewMultiDSConfig dewMultiDSConfig;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    public MybatisStarterConfiguration(MybatisPlusProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
    }

    @PostConstruct
    public void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }
        registerBeanDefinitions();
    }

    private void registerBeanDefinitions() {
        try {
            // 主数据源注入
            DataSource primaryDataSource = (DataSource) applicationContext.getBean("dataSource");
            registerBeanDefinitions(primaryDataSource, "primary");

            // 从数据源注入
            Set<String> datasourceNames = dewMultiDSConfig.getMultiDatasources().keySet();
            for (String datasourceName : datasourceNames) {
                if (applicationContext.containsBean(datasourceName + "JdbcTemplate")) {
                    // Registry SqlSessionFactory
                    DataSource dataSource = ((JdbcTemplate) applicationContext.getBean(datasourceName + "JdbcTemplate")).getDataSource();
                    registerBeanDefinitions(dataSource, datasourceName);
                }
            }

        } catch (Exception e) {
            logger.error("dew sqlsessiontemplate init failed", e);
        }
    }

    private void registerBeanDefinitions(DataSource dataSource, String dataSourceName) throws Exception {
        // registry SqlSessionFactory
        AbstractBeanDefinition sqlSessionFactory = BeanDefinitionBuilder.rootBeanDefinition(DefaultSqlSessionFactory.class).addConstructorArgValue(configuration(dataSource)).getBeanDefinition();
        beanFactory.registerBeanDefinition(dataSourceName + "SqlSessionFactory", sqlSessionFactory);

        // Registry SqlSessionTemplate
        AbstractBeanDefinition sqlSessionTemplate = null;
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            sqlSessionTemplate = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionTemplate.class).addConstructorArgReference(dataSourceName + "SqlSessionFactory").addConstructorArgValue(executorType).getBeanDefinition();
        } else {
            sqlSessionTemplate = BeanDefinitionBuilder.rootBeanDefinition(SqlSessionTemplate.class).addConstructorArgReference(dataSourceName + "SqlSessionFactory").getBeanDefinition();
        }
        beanFactory.registerBeanDefinition(dataSourceName + "SqlSessionTemplate", sqlSessionTemplate);
    }

    private Configuration configuration(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new MybatisConfiguration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (Object customizer : this.configurationCustomizers) {
                ((ConfigurationCustomizer) customizer).customize(configuration);
            }
        }
        if (configuration != null) {
            configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        }
        factory.setConfiguration(configuration);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }
        if (!ObjectUtils.isEmpty(this.properties.getGlobalConfig())) {
            factory.setGlobalConfig(this.properties.getGlobalConfig().convertGlobalConfiguration());
        }
        return factory.getObject().getConfiguration();
    }

}
