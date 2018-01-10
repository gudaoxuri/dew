package com.ecfront.dew.gateway;

import com.ecfront.dew.core.DewCloudApplication;
import com.ecfront.dew.gateway.filter.LoggingFilter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
public class GatewayApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayApplication.class).web(true).run(args);
    }

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

}
