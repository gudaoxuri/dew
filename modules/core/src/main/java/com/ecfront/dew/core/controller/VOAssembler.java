package com.ecfront.dew.core.controller;

import com.ecfront.dew.core.entity.IdEntity;

/**
 * VO组装器
 *
 * @param <V>
 * @param <E>
 */
public interface VOAssembler<V extends Object, E extends IdEntity> {

    default V entityToVO(E entity) throws RuntimeException {
        return (V) entity;
    }

    default E voToEntity(V vo) throws RuntimeException {
        return (E) vo;
    }

}
