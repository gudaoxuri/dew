package com.tairanchina.csp.dew.example.cache;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 工程启动类
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
public class CacheExampleApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(CacheExampleApplication.class).run(args);
    }

}
