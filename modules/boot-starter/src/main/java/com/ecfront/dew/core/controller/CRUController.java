package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.service.CRUService;
import com.ecfront.dew.core.service.CRUService;

public interface CRUController<T extends CRUService, P, E> extends CRUVOController<T, P, E, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

