package com.tairanchina.csp.dew.core.service;

import com.tairanchina.csp.dew.core.Container;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.jdbc.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.ParameterizedType;

public interface DewService<T extends Dao<P, E>, P, E> {

    Logger logger = LoggerFactory.getLogger(DewService.class);

    default Class<E> getModelClazz() {
        Class<E> clazz = (Class<E>) Container.SERVICE_ENTITY_CONTAINER.get(this.getClass());
        if (clazz != null) {
            return clazz;
        }
        if (Proxy.class.isAssignableFrom(this.getClass())) {
            clazz = (Class<E>) (((ParameterizedType) this.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[2]);
        } else {
            clazz = (Class<E>) (((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[2]);
        }
        Container.SERVICE_ENTITY_CONTAINER.put(this.getClass(), clazz);
        return clazz;
    }

    default T getDao() {
        T dao = (T) Container.SERVICE_DAO_BEAN_CONTAINER.get(this.getClass());
        if (dao != null) {
            return dao;
        }
        Class<Dao<P, E>> dewDaoClass;
        if (Proxy.class.isAssignableFrom(this.getClass())) {
            dewDaoClass = (Class<Dao<P, E>>) (((ParameterizedType) this.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        } else {
            dewDaoClass = (Class<Dao<P, E>>) (((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        }
        dao = (T) Dew.applicationContext.getBean(dewDaoClass);
        Container.SERVICE_DAO_BEAN_CONTAINER.put(this.getClass(), dao);
        return dao;
    }

}
