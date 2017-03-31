package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUDSService;

public interface CRUDSController<T extends CRUDSService, E extends IdEntity> extends CRUDController<T, E>, CRUSController<T, E> {

}

