package com.ecfront.dew.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {FreeMarkerAutoConfiguration.class, GsonAutoConfiguration.class})
@ComponentScan(basePackageClasses = {Dew.class})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableCaching(proxyTargetClass = true)
public abstract class DewBootApplication {

    @Autowired
    private Dew dew;

}
