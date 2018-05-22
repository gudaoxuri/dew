package com.tairanchina.csp.dew.core.service;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.core.jdbc.Dao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CRUService<T extends Dao<P, E>, P, E> extends DewService<T, P, E> {

    default Resp<Optional<Object>> preGetById(P id) {
        return Resp.success(Optional.empty());
    }

    default Resp<Optional<Object>> preGetByCode(String code) {
        return Resp.success(Optional.empty());
    }

    default E postGet(E entity, Optional<Object> preBody) {
        return entity;
    }

    default Resp<Optional<Object>> preFind() {
        return Resp.success(Optional.empty());
    }

    default List<E> postFind(List<E> entities, Optional<Object> preBody) {
        return entities;
    }

    default Resp<Optional<Object>> prePaging() {
        return Resp.success(Optional.empty());
    }

    default Page<E> postPaging(Page<E> entities, Optional<Object> preBody) {
        return entities;
    }

    default Resp<Optional<Object>> preExistById(P id) {
        return Resp.success(Optional.empty());
    }

    default void postExistById(P id, Optional<Object> preBody) {
    }

    default Resp<Optional<Object>> preExistByCode(String code) {
        return Resp.success(Optional.empty());
    }

    default void postExistByCode(String code, Optional<Object> preBody) {
    }

    default Resp<Optional<Object>> preSave(E entity) {
        return Resp.success(Optional.empty());
    }

    default E postSave(E entity, Optional<Object> preBody) {
        return entity;
    }

    default Resp<Optional<Object>> preUpdateById(P id, E entity) {
        return Resp.success(Optional.empty());
    }

    default Resp<Optional<Object>> preUpdateByCode(String code, E entity) {
        return Resp.success(Optional.empty());
    }

    default E postUpdate(E entity, Optional<Object> preBody) {
        return entity;
    }

    default Resp<E> getById(P id) {
        logger.debug("[{}] GetById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preGetById(id);
        if (preResult.ok()) {
            return Resp.success(postGet(getDao().getById(id), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<E> getByCode(String code) {
        logger.debug("[{}] GetByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preGetByCode(code);
        if (preResult.ok()) {
            return Resp.success(postGet(getDao().getByCode(code), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<List<E>> find() {
        logger.debug("[{}] Find.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDao().findAll(), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Page<E>> paging(long pageNumber, int pageSize) {
        logger.debug("[{}] Paging {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize);
        Resp<Optional<Object>> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDao().paging(pageNumber, pageSize), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> save(E entity) {
        logger.debug("[{}] Save.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preSave(entity);
        if (preResult.ok()) {
            return Resp.success(postSave(getDao().getById(getDao().insert(entity)), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> updateById(P id, E entity) {
        logger.debug("[{}] UpdateById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preUpdateById(id, entity);
        if (preResult.ok()) {
            getDao().updateById(id, entity);
            return Resp.success(postUpdate(getDao().getById(id), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> updateByCode(String code, E entity) {
        logger.debug("[{}] UpdateByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preUpdateByCode(code, entity);
        if (preResult.ok()) {
            getDao().updateByCode(code, entity);
            return Resp.success(postUpdate(getDao().getByCode(code), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Boolean> existById(P id) {
        logger.debug("[{}] ExistById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preExistById(id);
        if (preResult.ok()) {
            boolean result = getDao().existById(id);
            postExistById(id, preResult.getBody());
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Boolean> existByCode(String code) {
        logger.debug("[{}] ExistByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preExistByCode(code);
        if (preResult.ok()) {
            boolean result = getDao().existByCode(code);
            postExistByCode(code, preResult.getBody());
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
