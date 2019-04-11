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

package ms.dew.core.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import ms.dew.core.DewCloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Notify auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnProperty(prefix = "dew.cloud.error", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DewCloudConfig.class)
public class NotifyAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(NotifyAutoConfiguration.class);

    @Autowired
    private DewCloudConfig dewCloudConfig;

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        if (dewCloudConfig.getError().isEnabled()) {
            logger.info("Enabled Failure Event Notifier");

            HystrixPlugins.reset();

            HystrixConcurrencyStrategy concurrencyStrategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(concurrencyStrategy);
            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
            HystrixPlugins.getInstance().registerEventNotifier(new FailureEventNotifier(dewCloudConfig));
        }
    }

}
