package com.tairanchina.csp.dew.core.service;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.core.jdbc.Dao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CRUSService<T extends Dao<P, E>, P, E> extends CRUService<T, P, E> {

    default Resp<Optional<Object>> preEnableById(P id) {
        return Resp.success(Optional.empty());
    }

    default Resp<Optional<Object>> preEnableByCode(String code) {
        return Resp.success(Optional.empty());
    }

    default void postEnableById(P id, Optional<Object> preBody) {
    }

    default void postEnableByCode(String code, Optional<Object> preBody) {
    }

    default Resp<Optional<Object>> preDisableById(P id) {
        return Resp.success(Optional.empty());
    }

    default void postDisableById(P id, Optional<Object> preBody) {
    }

    default Resp<Optional<Object>> preDisableByCode(String code) {
        return Resp.success(Optional.empty());
    }

    default void postDisableByCode(String code, Optional<Object> preBody) {
    }

    default Resp<List<E>> findEnabled() {
        logger.debug("[{}] FindEnable.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDao().findEnabled(), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<List<E>> findDisabled() {
        logger.debug("[{}] FindDisable.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDao().findDisabled(), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Page<E>> pagingEnabled(long pageNumber, int pageSize) {
        logger.debug("[{}] PagingEnable {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize);
        Resp<Optional<Object>> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDao().pagingEnabled(pageNumber, pageSize), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Page<E>> pagingDisabled(long pageNumber, int pageSize) {
        logger.debug("[{}] PagingDisable {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize);
        Resp<Optional<Object>> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDao().pagingDisabled(pageNumber, pageSize), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> enableById(P id) {
        logger.debug("[{}] EnableById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preEnableById(id);
        if (preResult.ok()) {
            getDao().enableById(id);
            postEnableById(id, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> enableByCode(String code) {
        logger.debug("[{}] EnableByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preEnableByCode(code);
        if (preResult.ok()) {
            getDao().enableByCode(code);
            postEnableByCode(code, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> disableById(P id) {
        logger.debug("[{}] DisableById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preDisableById(id);
        if (preResult.ok()) {
            getDao().disableById(id);
            postDisableById(id, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> disableByCode(String code) {
        logger.debug("[{}] DisableByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preDisableByCode(code);
        if (preResult.ok()) {
            getDao().disableByCode(code);
            postDisableByCode(code, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
