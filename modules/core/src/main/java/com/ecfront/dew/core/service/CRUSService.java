package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CRUSService<T extends DewRepository<E>, E extends IdEntity> extends CRUService<T, E> {

    default Resp<Void> preEnableById(long id) throws RuntimeException {
        return Resp.success(null);
    }

    default Resp<Void> preEnableByCode(String code) throws RuntimeException {
        return Resp.success(null);
    }

    default void postEnableById(long id) throws RuntimeException {
    }

    default void postEnableByCode(String code) throws RuntimeException {
    }

    default Resp<Void> preDisableById(long id) throws RuntimeException {
        return Resp.success(null);
    }

    default void postDisableById(long id) throws RuntimeException {
    }

    default Resp<Void> preDisableByCode(String code) throws RuntimeException {
        return Resp.success(null);
    }

    default void postDisableByCode(String code) throws RuntimeException {
    }

    default Resp<List<E>> findEnable() throws RuntimeException {
        logger.debug("[{}] FindEnable.", getModelClazz().getSimpleName());
        Resp<Void> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDewRepository().findEnable()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<List<E>> findDisable() throws RuntimeException {
        logger.debug("[{}] FindDisable.", getModelClazz().getSimpleName());
        Resp<Void> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDewRepository().findDisable()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize) throws RuntimeException {
        return pagingEnable(pageNumber, pageSize, null);
    }

    default Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] PagingEnable {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Void> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDewRepository().pagingEnable(pageNumber, pageSize, sort)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize) throws RuntimeException {
        return pagingDisable(pageNumber, pageSize, null);
    }

    default Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] PagingDisable {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Void> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDewRepository().pagingDisable(pageNumber, pageSize, sort)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> enableById(long id) throws RuntimeException {
        logger.debug("[{}] EnableById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Void> preResult = preEnableById(id);
        if (preResult.ok()) {
            getDewRepository().enableById(id);
            postEnableById(id);
            return Resp.success(null);
        }
        return preResult;
    }

    @Transactional
    default Resp<Void> enableByCode(String code) throws RuntimeException {
        logger.debug("[{}] EnableByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Void> preResult = preEnableByCode(code);
        if (preResult.ok()) {
            getDewRepository().enableByCode(code);
            postEnableByCode(code);
            return Resp.success(null);
        }
        return preResult;
    }

    @Transactional
    default Resp<Void> disableById(long id) throws RuntimeException {
        logger.debug("[{}] DisableById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Void> preResult = preDisableById(id);
        if (preResult.ok()) {
            getDewRepository().disableById(id);
            postDisableById(id);
            return Resp.success(null);
        }
        return preResult;
    }

    @Transactional
    default Resp<Void> disableByCode(String code) throws RuntimeException {
        logger.debug("[{}] DisableByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Void> preResult = preDisableByCode(code);
        if (preResult.ok()) {
            getDewRepository().disableByCode(code);
            postDisableByCode(code);
            return Resp.success(null);
        }
        return preResult;
    }

}
