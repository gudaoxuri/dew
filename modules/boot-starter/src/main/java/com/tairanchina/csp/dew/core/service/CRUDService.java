package com.tairanchina.csp.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.core.jdbc.Dao;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CRUDService<T extends Dao<P, E>, P, E> extends CRUService<T, P, E> {

    default Resp<Optional<Object>> preDeleteById(P id){
        return Resp.success(Optional.empty());
    }

    default void postDeleteById(P id, Optional<Object> preBody){
    }

    default Resp<Optional<Object>> preDeleteByCode(String code){
        return Resp.success(Optional.empty());
    }

    default void postDeleteByCode(String code, Optional<Object> preBody){
    }

    @Transactional
    default Resp<Void> deleteById(P id){
        logger.debug("[{}] DeleteById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preDeleteById(id);
        if (preResult.ok()) {
            getDao().deleteById(id);
            postDeleteById(id, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> deleteByCode(String code){
        logger.debug("[{}] DeleteByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preDeleteByCode(code);
        if (preResult.ok()) {
            getDao().deleteByCode(code);
            postDeleteByCode(code, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
