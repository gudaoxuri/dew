package com.tairanchina.csp.dew.core.logger;

import feign.Client;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by hzlizx on 2018/8/7 0007
 */
public class TraceLogBeanPostProcessor implements BeanPostProcessor {

    private final BeanFactory beanFactory;

    TraceLogBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof Client && !(bean instanceof TraceLogFeignClient)){
            return new TraceLogFeignClient(beanFactory);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
