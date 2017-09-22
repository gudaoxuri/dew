package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.service.CRUDSService;

public interface CRUDSController<T extends CRUDSService,P, E> extends CRUDController<T,P, E>, CRUSController<T,P, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

