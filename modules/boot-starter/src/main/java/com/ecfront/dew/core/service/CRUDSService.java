package com.ecfront.dew.core.service;

import com.ecfront.dew.core.jdbc.Dao;
import com.ecfront.dew.core.jdbc.Dao;

public interface CRUDSService<T extends Dao<P, E>, P, E> extends CRUSService<T, P, E>, CRUDService<T, P, E> {

}
