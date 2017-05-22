package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUService;

public interface CRUController<T extends CRUService, E extends IdEntity> extends CRUVOController<T, E, E> {

}

