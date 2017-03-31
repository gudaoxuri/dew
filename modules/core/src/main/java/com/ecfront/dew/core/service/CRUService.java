package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CRUService<T extends DewRepository<E>, E extends IdEntity> extends DewService<T, E> {

    default Resp<Optional<Object>> preGetById(long id) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default Resp<Optional<Object>> preGetByCode(String code) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default E postGet(E entity, Optional<Object> preBody) throws RuntimeException {
        return entity;
    }

    default Resp<Optional<Object>> preFind() throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default List<E> postFind(List<E> entities, Optional<Object> preBody) throws RuntimeException {
        return entities;
    }

    default Resp<Optional<Object>> prePaging() throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default PageDTO<E> postPaging(PageDTO<E> entities, Optional<Object> preBody) throws RuntimeException {
        return entities;
    }

    default Resp<Optional<Object>> preExistById(long id) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default void postExistById(long id, Optional<Object> preBody) throws RuntimeException {
    }

    default Resp<Optional<Object>> preExistByCode(String code) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default void postExistByCode(String code, Optional<Object> preBody) throws RuntimeException {
    }

    default Resp<Optional<Object>> preSave(E entity) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default E postSave(E entity, Optional<Object> preBody) throws RuntimeException {
        return entity;
    }

    default Resp<Optional<Object>> preUpdateById(long id, E entity) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default Resp<Optional<Object>> preUpdateByCode(String code, E entity) throws RuntimeException {
        return Resp.success(Optional.empty());
    }

    default E postUpdate(E entity, Optional<Object> preBody) throws RuntimeException {
        return entity;
    }

    default Resp<E> getById(long id) throws RuntimeException {
        logger.debug("[{}] GetById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preGetById(id);
        if (preResult.ok()) {
            return Resp.success(postGet(getDewRepository().getById(id), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<E> getByCode(String code) throws RuntimeException {
        logger.debug("[{}] GetByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preGetByCode(code);
        if (preResult.ok()) {
            return Resp.success(postGet(getDewRepository().getByCode(code), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<List<E>> find() throws RuntimeException {
        logger.debug("[{}] Find.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(getDewRepository().findAll(), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<PageDTO<E>> paging(int pageNumber, int pageSize) throws RuntimeException {
        return paging(pageNumber, pageSize, null);
    }

    default Resp<PageDTO<E>> paging(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] Paging {} {} {}.", getModelClazz().getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Optional<Object>> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(getDewRepository().paging(pageNumber, pageSize, sort), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> save(E entity) throws RuntimeException {
        logger.debug("[{}] Save.", getModelClazz().getSimpleName());
        Resp<Optional<Object>> preResult = preSave(entity);
        if (preResult.ok()) {
            return Resp.success(postSave(getDewRepository().save(entity), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> updateById(long id, E entity) throws RuntimeException {
        logger.debug("[{}] UpdateById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preUpdateById(id, entity);
        if (preResult.ok()) {
            return Resp.success(postUpdate(getDewRepository().updateById(id, entity), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<E> updateByCode(String code, E entity) throws RuntimeException {
        logger.debug("[{}] UpdateByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preUpdateByCode(code, entity);
        if (preResult.ok()) {
            return Resp.success(postUpdate(getDewRepository().updateByCode(code, entity), preResult.getBody()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Boolean> existById(long id) throws RuntimeException {
        logger.debug("[{}] ExistById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Optional<Object>> preResult = preExistById(id);
        if (preResult.ok()) {
            boolean result = getDewRepository().existById(id);
            postExistById(id, preResult.getBody());
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    default Resp<Boolean> existByCode(String code) throws RuntimeException {
        logger.debug("[{}] ExistByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Optional<Object>> preResult = preExistByCode(code);
        if (preResult.ok()) {
            boolean result = getDewRepository().existByCode(code);
            postExistByCode(code, preResult.getBody());
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
