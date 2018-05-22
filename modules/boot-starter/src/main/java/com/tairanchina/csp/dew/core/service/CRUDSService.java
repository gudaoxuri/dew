package com.tairanchina.csp.dew.core.service;

import com.tairanchina.csp.dew.core.jdbc.Dao;

public interface CRUDSService<T extends Dao<P, E>, P, E> extends CRUSService<T, P, E>, CRUDService<T, P, E> {

}
