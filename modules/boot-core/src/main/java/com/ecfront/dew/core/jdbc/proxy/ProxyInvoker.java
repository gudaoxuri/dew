package com.ecfront.dew.core.jdbc.proxy;

import org.springframework.cglib.proxy.Proxy;

/**
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