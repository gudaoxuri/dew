package com.tairanchina.csp.dew.core.controller;


import com.tairanchina.csp.dew.core.service.CRUDSService;

public interface CRUDSController<T extends CRUDSService,P, E> extends CRUDController<T,P, E>, CRUSController<T,P, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

