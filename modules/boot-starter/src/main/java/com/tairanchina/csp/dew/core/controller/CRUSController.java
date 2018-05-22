package com.tairanchina.csp.dew.core.controller;


import com.tairanchina.csp.dew.core.service.CRUSService;

public interface CRUSController<T extends CRUSService, P, E> extends CRUSVOController<T, P, E, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

