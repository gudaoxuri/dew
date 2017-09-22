package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.service.CRUSService;

public interface CRUSController<T extends CRUSService, P, E> extends CRUSVOController<T, P, E, E> {

    @Override
    default boolean convertAble() {
        return false;
    }

}

