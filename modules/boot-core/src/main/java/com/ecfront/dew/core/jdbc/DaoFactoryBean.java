package com.ecfront.dew.core.jdbc;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.jdbc.proxy.ProxyInvoker;
import org.springframework.beans.factory.FactoryBean;

/**
 * 接口动态实现
 */
public class DaoFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        return Dew.applicationContext.containsBean(mapperInterface.getName()) ?
                Dew.applicationContext.getBean(mapperInterface) : new ProxyInvoker().getInstance(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
