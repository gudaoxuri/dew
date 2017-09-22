package com.ecfront.dew.core.service;

import com.ecfront.dew.core.jdbc.DewDao;

public interface CRUDSService<T extends DewDao<P, E>, P, E> extends CRUSService<T, P, E>, CRUDService<T, P, E> {

}
