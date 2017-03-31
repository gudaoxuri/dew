package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUDService;

public interface CRUDController<T extends CRUDService, E extends IdEntity> extends CRUDVOController<T, E, E> {

}

