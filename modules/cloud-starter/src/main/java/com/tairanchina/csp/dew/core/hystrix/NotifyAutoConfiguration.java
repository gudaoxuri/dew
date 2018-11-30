package com.tairanchina.csp.dew.core.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.tairanchina.csp.dew.core.DewCloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(prefix = "dew.cloud.error", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DewCloudConfig.class)
public class NotifyAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(NotifyAutoConfiguration.class);

    @Autowired
    private DewCloudConfig dewCloudConfig;

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        if (dewCloudConfig.getError().isEnabled()) {
            logger.info("Enabled Failure Event Notifier");

            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
            HystrixConcurrencyStrategy concurrencyStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();

            HystrixPlugins.reset();

            HystrixPlugins.getInstance().registerConcurrencyStrategy(concurrencyStrategy);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(new FailureEventNotifier(dewCloudConfig));
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        }
    }

}
