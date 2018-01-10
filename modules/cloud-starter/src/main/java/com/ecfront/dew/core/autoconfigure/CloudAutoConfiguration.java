package com.ecfront.dew.core.autoconfigure;

import ch.qos.logback.classic.Level;
import com.ecfront.dew.core.loding.DewLoadImmediately;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.ecfront.dew.core.DewCloudConfig;
import com.ecfront.dew.core.loding.DewLoadImmediately;
import com.ecfront.dew.core.logger.DewLoggerWebMvcConfigurer;
import com.ecfront.dew.core.logger.DewTraceLogWrap;
import com.ecfront.dew.core.logger.DewTraceRestTemplateInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
@DewLoadImmediately
public class CloudAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CloudAutoConfiguration.class);

    @Autowired
    private DewCloudConfig dewCloudConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private DewLoggerWebMvcConfigurer dewLoggerWebMvcConfigurer;

    @Autowired(required = false)
    private HystrixEventNotifier hystrixEventNotifier;

    @Bean
    @LoadBalanced
    protected RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        if (!dewCloudConfig.getError().getNotifyEmails().isEmpty() && hystrixEventNotifier != null) {
            logger.info("Enabled Failure Event Notifier");
            HystrixPlugins.getInstance().registerEventNotifier(hystrixEventNotifier);
        }
        if (dewCloudConfig.getTraceLog().isEnabled()) {
            logger.info("Enabled Trace Log");
            restTemplate.getInterceptors().add(new DewTraceRestTemplateInterceptor());
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DewTraceLogWrap.class);
            root.setLevel(Level.TRACE);
        }
    }

}