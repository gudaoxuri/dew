package com.tairanchina.csp.dew.core.controller;


import com.tairanchina.csp.dew.core.service.CRUDSService;

public interface CRUDSVOController<T extends CRUDSService, P, V, E> extends CRUDVOController<T, P, V, E>, CRUSVOController<T, P, V, E> {

    @Override
    default boolean convertAble() {
        return true;
    }

}

