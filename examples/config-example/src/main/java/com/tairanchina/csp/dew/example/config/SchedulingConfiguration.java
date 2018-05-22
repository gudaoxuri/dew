package com.tairanchina.csp.dew.example.config;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * SchedulingConfiguration
 *
 * @author hzzjb
 * @date 2017/9/18
 */
@Configuration
@EnableScheduling
public class SchedulingConfiguration implements SchedulingConfigurer {

    private Logger logger = LoggerFactory.getLogger(SchedulingConfiguration.class);

    @Autowired
    private ConfigExampleConfig config;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> logger.info("task1: " + config.getVersion()), triggerContext -> {
            Instant instant = Instant.now().plus(5, SECONDS);
            return Date.from(instant);
        });

        taskRegistrar.addTriggerTask(() -> logger.info("task2: " + config.getVersion()), new CronTrigger("1/3 * * * * ?"));
    }
}
