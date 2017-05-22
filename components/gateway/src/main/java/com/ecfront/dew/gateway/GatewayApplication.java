package com.ecfront.dew.gateway;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewCloudApplication;
import com.ecfront.dew.gateway.auth.AuthFilter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableZuulProxy
@ComponentScan(basePackageClasses = {Dew.class, GatewayApplication.class})
public class GatewayApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayApplication.class).web(true).run(args);
    }

    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }

    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        return new PatternServiceRouteMapper("(?<name>^.+)", "${name}");
    }

}
