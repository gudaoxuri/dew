package com.tairanchina.csp.dew.example.sleuth;

import ch.qos.logback.classic.PatternLayout;
import com.tairanchina.csp.dew.example.sleuth.logger.TestConverter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringCloudApplication
@EnableFeignClients
public class SleuthExampleApplication{

    public static void main(String[] args) {
        PatternLayout.defaultConverterMap.put("dew", TestConverter.class.getName());
        new SpringApplicationBuilder(SleuthExampleApplication.class).run(args);
    }

}
