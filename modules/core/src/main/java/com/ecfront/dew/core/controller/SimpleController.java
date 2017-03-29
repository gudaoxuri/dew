package com.ecfront.dew.core.controller;

import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleController<T extends SimpleServiceImpl, E extends IdEntity> extends SimpleVOController<T, E, E> {

    protected static final Logger logger = LoggerFactory.getLogger(SimpleController.class);
}

