package com.tairanchina.csp.dew.core.logger;

import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.io.IOException;

/**
 * 重写Client的execute方法，每次调用前后都使用DewTraceLogWrap打日志。
 * Created by hzlizx on 2018/8/7 0007
 */
public class TraceLogFeignClient implements Client {

    private final Client delegate;

    TraceLogFeignClient(BeanFactory beanFactory) {
        this.delegate = client(beanFactory);
    }

    private Client client(BeanFactory beanFactory) {
        try {
            return beanFactory.getBean(Client.class);
        } catch (NoSuchBeanDefinitionException e) {
            return new Client.Default(null, null);
        }
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        DewTraceLogWrap.request("FeignClient",request.method(),request.url());
        Response response = this.delegate.execute(request, options);
        DewTraceLogWrap.response("FeignClient",response.status(),response.request().method(),response.request().url());
        return response;
    }
}
