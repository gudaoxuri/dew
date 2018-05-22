package com.tairanchina.csp.dew.core.controller;


import com.tairanchina.csp.dew.core.service.CRUDService;

public interface CRUDController<T extends CRUDService, P, E> extends CRUDVOController<T, P, E, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

