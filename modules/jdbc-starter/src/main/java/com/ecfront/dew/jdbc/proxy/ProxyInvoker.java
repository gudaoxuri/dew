package com.ecfront.dew.jdbc.proxy;

import org.springframework.cglib.proxy.Proxy;

/**
 * Created by 迹_Jason on 2017/7/26.
 * 代理调用
 */
public class ProxyInvoker {

    public <T> T getInstance(Class<T> cls) {
        return (T) Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                new MethodProxy());
    }

}