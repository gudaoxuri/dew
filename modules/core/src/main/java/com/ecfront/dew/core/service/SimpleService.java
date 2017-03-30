package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SimpleService<T extends DewRepository<E>, E extends IdEntity> {

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

    default Resp<Boolean> preEnableById(long id) throws RuntimeException {
        return Resp.success(true);
    }

    default Resp<Boolean> preEnableByCode(String code) throws RuntimeException {
        return Resp.success(true);
    }

    default void postEnableById(long id) throws RuntimeException {
    }

    default void postEnableByCode(String code) throws RuntimeException {
    }

    default Resp<Boolean> preDisableById(long id) throws RuntimeException {
        return Resp.success(true);
    }

    default void postDisableById(long id) throws RuntimeException {
    }

    default Resp<Boolean> preDisableByCode(String code) throws RuntimeException {
        return Resp.success(true);
    }

    default void postDisableByCode(String code) throws RuntimeException {
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

    default Resp<Boolean> preDeleteById(long id) throws RuntimeException {
        return Resp.success(true);
    }

    default void postDeleteById(long id) throws RuntimeException {
    }

    default Resp<Boolean> preDeleteByCode(String code) throws RuntimeException {
        return Resp.success(true);
    }

    default void postDeleteByCode(String code) throws RuntimeException {
    }

    Resp<E> getById(long id) throws RuntimeException;

    Resp<E> getByCode(String code) throws RuntimeException;

    Resp<List<E>> find() throws RuntimeException;

    Resp<List<E>> findEnable() throws RuntimeException;

    Resp<List<E>> findDisable() throws RuntimeException;

    Resp<PageDTO<E>> paging(int pageNumber, int pageSize) throws RuntimeException;

    Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize) throws RuntimeException;

    Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize) throws RuntimeException;

    Resp<PageDTO<E>> paging(int pageNumber, int pageSize, Sort sort) throws RuntimeException;

    Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize, Sort sort) throws RuntimeException;

    Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize, Sort sort) throws RuntimeException;

    Resp<Void> enableById(long id) throws RuntimeException;

    Resp<Void> enableByCode(String code) throws RuntimeException;

    Resp<Void> disableById(long id) throws RuntimeException;

    Resp<Void> disableByCode(String code) throws RuntimeException;

    Resp<E> save(E entity) throws RuntimeException;

    Resp<E> updateById(long id, E entity) throws RuntimeException;

    Resp<E> updateByCode(String code, E entity) throws RuntimeException;

    Resp<Void> deleteById(long id) throws RuntimeException;

    Resp<Void> deleteByCode(String code) throws RuntimeException;

    Resp<Boolean> existById(long id) throws RuntimeException;

    Resp<Boolean> existByCode(String code) throws RuntimeException;
}
