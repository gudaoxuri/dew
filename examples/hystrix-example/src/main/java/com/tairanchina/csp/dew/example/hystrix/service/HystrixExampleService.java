package com.tairanchina.csp.dew.example.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
/**
 * http://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#_circuit_breaker_hystrix_clients
 */
public class HystrixExampleService {

    private static final Logger logger = LoggerFactory.getLogger(HystrixExampleService.class);

    @HystrixCommand(fallbackMethod = "defaultStores", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })
    public String getStores(Map<String, Object> parameters, DewContext context) {
        // ！！！ Hystrix使用新线程执行代码，导致Threadlocal数据不能同步，
        // 使用时需要将用到的数据做为参数传入，如果需要使用Dew框架的上下文需要先传入再设值
        DewContext.setContext(context);
        try {
            Thread.sleep(new Random().nextInt(3000));
            logger.info("Normal Service Token:" + Dew.context().getToken());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "ok";
    }

    public String defaultStores(Map<String, Object> parameters, DewContext context, Throwable e) {
        DewContext.setContext(context);
        logger.info("Error Service Token:" + Dew.context().getToken());
        return "fail";
    }

}
