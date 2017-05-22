package com.ecfront.dew.core.service;

import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;

public interface CRUDSService<T extends DewRepository<E>, E extends IdEntity> extends CRUSService<T, E>, CRUDService<T, E> {

}
