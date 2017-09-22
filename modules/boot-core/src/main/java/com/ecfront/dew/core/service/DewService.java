package com.ecfront.dew.core.service;

import com.ecfront.dew.core.Container;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.jdbc.DewDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.ParameterizedType;

public interface DewService<T extends DewDao<P, E>, P, E> {

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
        Class<DewDao<P, E>> dewDaoClass;
        if (Proxy.class.isAssignableFrom(this.getClass())) {
            dewDaoClass = (Class<DewDao<P, E>>) (((ParameterizedType) this.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        } else {
            dewDaoClass = (Class<DewDao<P, E>>) (((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        }
        dao = (T) Dew.applicationContext.getBean(dewDaoClass);
        Container.SERVICE_DAO_BEAN_CONTAINER.put(this.getClass(), dao);
        return dao;
    }

}
