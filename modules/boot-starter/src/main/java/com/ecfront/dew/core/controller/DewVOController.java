package com.ecfront.dew.core.controller;


import com.ecfront.dew.Dew;
import com.ecfront.dew.core.Container;
import com.ecfront.dew.core.service.DewService;
import com.ecfront.dew.core.Container;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.service.DewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.ParameterizedType;

public interface DewVOController<T extends DewService, V, E> extends VOAssembler<V, E> {

    Logger logger = LoggerFactory.getLogger(DewVOController.class);

    default T getService() {
        T service = (T) Container.CONTROLLER_SERVICE_BEAN_CONTAINER.get(this.getClass());
        if (service != null) {
            return service;
        }
        Class<DewService> dewServiceClass;
        if (Proxy.class.isAssignableFrom(this.getClass())) {
            dewServiceClass = (Class<DewService>) (((ParameterizedType) this.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        } else {
            dewServiceClass = (Class<DewService>) (((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        }
        service = (T) Dew.applicationContext.getBean(dewServiceClass);
        Container.CONTROLLER_SERVICE_BEAN_CONTAINER.put(this.getClass(), service);
        return service;
    }

}

