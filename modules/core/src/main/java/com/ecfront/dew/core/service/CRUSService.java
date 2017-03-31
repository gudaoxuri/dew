package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CRUSService<T extends DewRepository<E>, E extends IdEntity> extends CRUService<T, E> {

    default Resp<Optional<Object>> preEnableById(long id) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default Resp<Optional<Object>> preEnableByCode(String code) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default void postEnableById(long id, Optional<Object> preBody) throws RuntimeException {
    }

    default void postEnableByCode(String code, Optional<Object> preBody) throws RuntimeException {
    }

    default Resp<Optional<Object>> preDisableById(long id) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default void postDisableById(long id, Optional<Object> preBody) throws RuntimeException {
    }

    default Resp<Optional<Object>> preDisableByCode(String code) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default void postDisableByCode(String code, Optional<Object> preBody) throws RuntimeException {
    }

    default Resp<List<E>> findEnable() throws RuntimeException {
        logger.debug("[{}] FindEnable.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDewRepository().findEnable(), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<List<E>> findDisable() throws RuntimeException {
        logger.debug("[{}] FindDisable.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDewRepository().findDisable(), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize) throws RuntimeException {
        return pagingEnable(pageNumber, pageSize, null);
    }

    default Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] PagingEnable {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Optional<Object>> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDewRepository().pagingEnable(pageNumber, pageSize, sort), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize) throws RuntimeException {
        return pagingDisable(pageNumber, pageSize, null);
    }

    default Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] PagingDisable {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Optional<Object>> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDewRepository().pagingDisable(pageNumber, pageSize, sort), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> enableById(long id) throws RuntimeException {
        logger.debug("[{}] EnableById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preEnableById(id);
        if (preResult.ok()) {
            getDewRepository().enableById(id);
            postEnableById(id, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> enableByCode(String code) throws RuntimeException {
        logger.debug("[{}] EnableByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preEnableByCode(code);
        if (preResult.ok()) {
            getDewRepository().enableByCode(code);
            postEnableByCode(code, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> disableById(long id) throws RuntimeException {
        logger.debug("[{}] DisableById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preDisableById(id);
        if (preResult.ok()) {
            getDewRepository().disableById(id);
            postDisableById(id, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> disableByCode(String code) throws RuntimeException {
        logger.debug("[{}] DisableByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preDisableByCode(code);
        if (preResult.ok()) {
            getDewRepository().disableByCode(code);
            postDisableByCode(code, preResult.getBody());
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
