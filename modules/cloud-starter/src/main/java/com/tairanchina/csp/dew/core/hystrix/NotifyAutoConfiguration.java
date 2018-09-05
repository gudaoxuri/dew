package com.tairanchina.csp.dew.core.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.tairanchina.csp.dew.core.DewCloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(prefix = "dew.cloud.error", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties(DewCloudConfig.class)
public class NotifyAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(NotifyAutoConfiguration.class);

    @Autowired
    private DewCloudConfig dewCloudConfig;

    @Value("${spring.mail.username:dew:}")
    private String emailFrom;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value(("${spring.profiles.active:default}"))
    private String profile;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        if (!dewCloudConfig.getError().getNotifyEmails().isEmpty()
                && dewCloudConfig.getError().isEnabled()
                && mailSender != null
                && !StringUtils.isEmpty(emailFrom)) {
            logger.info("Enabled Failure Event Notifier");
            HystrixPlugins.getInstance().registerEventNotifier(new FailureEventNotifier(dewCloudConfig, mailSender, emailFrom, applicationName, profile));
        }
    }

}
