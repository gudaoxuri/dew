package com.tairanchina.csp.dew.example.rest;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工程启动类
 */
@ComponentScan(basePackageClasses = {Dew.class, ServiceExampleApplication.class})
public class ServiceExampleApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceExampleApplication.class).run(args);
    }

}
