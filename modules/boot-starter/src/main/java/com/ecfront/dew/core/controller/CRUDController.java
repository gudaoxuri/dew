package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.service.CRUDService;
import com.ecfront.dew.core.service.CRUDService;

public interface CRUDController<T extends CRUDService, P, E> extends CRUDVOController<T, P, E, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

