package com.ecfront.dew.core.controller;


import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUSService;

public interface CRUSController<T extends CRUSService, E extends IdEntity> extends CRUSVOController<T, E, E> {

}

