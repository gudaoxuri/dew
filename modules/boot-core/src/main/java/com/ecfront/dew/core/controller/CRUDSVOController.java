package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUDSService;

public interface CRUDSVOController<T extends CRUDSService, V extends Object, E extends IdEntity> extends CRUDVOController<T, V, E>, CRUSVOController<T, V, E> {

}

