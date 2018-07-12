package com.tairanchina.csp.dew.example.sleuth;


import com.tairanchina.csp.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@SpringCloudApplication
@EnableFeignClients
@Configuration
public class SleuthExampleApplication  {

    private Logger logger = LoggerFactory.getLogger(SleuthExampleApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(SleuthExampleApplication.class).run(args);
    }

    @PostConstruct
    public void init(){
        logger.info("开始监听.." );
        Dew.cluster.mq.subscribe("test712", message -> logger.info("pub_sub->{}" , message));
    }
}
