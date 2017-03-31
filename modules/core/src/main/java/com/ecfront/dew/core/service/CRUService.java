package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CRUService<T extends DewRepository<E>, E extends IdEntity> extends DewService<T, E> {

    default Resp<Boolean> preGetById(long id) throws RuntimeException {
        return Resp.success(true);
    }

    default Resp<Boolean> preGetByCode(String code) throws RuntimeException {
        return Resp.success(true);
    }

    default E postGet(E entity) throws RuntimeException {
        return entity;
    }

    default Resp<Boolean> preFind() throws RuntimeException {
        return Resp.success(true);
    }

    default List<E> postFind(List<E> entities) throws RuntimeException {
        return entities;
    }

    default Resp<Boolean> prePaging() throws RuntimeException {
        return Resp.success(true);
    }

    default PageDTO<E> postPaging(PageDTO<E> entities) throws RuntimeException {
        return entities;
    }

    default Resp<Boolean> preExistById(long id) throws RuntimeException {
        return Resp.success(true);
    }

    default void postExistById(long id) throws RuntimeException {
    }

    default Resp<Boolean> preExistByCode(String code) throws RuntimeException {
        return Resp.success(true);
    }

    default void postExistByCode(String code) throws RuntimeException {
    }

    default Resp<Boolean> preSave(E entity) throws RuntimeException {
        return Resp.success(true);
    }

    default E postSave(E entity) throws RuntimeException {
        return entity;
    }

    default Resp<Boolean> preUpdateById(long id, E entity) throws RuntimeException {
        return Resp.success(true);
    }

    default Resp<Boolean> preUpdateByCode(String code, E entity) throws RuntimeException {
        return Resp.success(true);
    }

    default E postUpdate(E entity) throws RuntimeException {
        return entity;
    }

    default Resp<E> getById(long id) throws RuntimeException {
        logger.debug("[{}] GetById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Boolean> preResult = preGetById(id);
        if (preResult.ok()) {
            return Resp.success(postGet(getDewRepository().getById(id)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<E> getByCode(String code) throws RuntimeException {
        logger.debug("[{}] GetByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Boolean> preResult = preGetByCode(code);
        if (preResult.ok()) {
            return Resp.success(postGet(getDewRepository().getByCode(code)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<List<E>> find() throws RuntimeException {
        logger.debug("[{}] Find.", getModelClazz().getSimpleName());
        Resp<Boolean> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDewRepository().findAll()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<PageDTO<E>> paging(int pageNumber, int pageSize) throws RuntimeException {
        return paging(pageNumber, pageSize, null);
    }

    default Resp<PageDTO<E>> paging(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] Paging {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Boolean> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDewRepository().paging(pageNumber, pageSize, sort)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> save(E entity) throws RuntimeException {
        logger.debug("[{}] Save.", getModelClazz().getSimpleName());
        Resp<Boolean> preResult = preSave(entity);
        if (preResult.ok()) {
            return Resp.success(postSave(getDewRepository().save(entity)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> updateById(long id, E entity) throws RuntimeException {
        logger.debug("[{}] UpdateById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Boolean> preResult = preUpdateById(id, entity);
        if (preResult.ok()) {
            return Resp.success(postUpdate(getDewRepository().updateById(id, entity)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> updateByCode(String code, E entity) throws RuntimeException {
        logger.debug("[{}] UpdateByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Boolean> preResult = preUpdateByCode(code, entity);
        if (preResult.ok()) {
            return Resp.success(postUpdate(getDewRepository().updateByCode(code, entity)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Boolean> existById(long id) throws RuntimeException {
        logger.debug("[{}] ExistById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Boolean> preResult = preExistById(id);
        if (preResult.ok()) {
            boolean result = getDewRepository().existById(id);
            postExistById(id);
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Boolean> existByCode(String code) throws RuntimeException {
        logger.debug("[{}] ExistByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Boolean> preResult = preExistByCode(code);
        if (preResult.ok()) {
            boolean result = getDewRepository().existByCode(code);
            postExistByCode(code);
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
