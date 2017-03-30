package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SimpleService<T extends DewRepository<E>, E extends IdEntity> {

    default Resp<Boolean> preGet(long id) throws RuntimeException {
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

    default Resp<Boolean> preSave(E entity) throws RuntimeException {
        return Resp.success(true);
    }

    default E postSave(E entity) throws RuntimeException {
        return entity;
    }

    default Resp<Boolean> preUpdate(long id, E entity) throws RuntimeException {
        return Resp.success(true);
    }

    default E postUpdate(long id, E entity) throws RuntimeException {
        return entity;
    }

    default Resp<Boolean> preDelete(long id) throws RuntimeException {
        return Resp.success(true);
    }

    default long postDelete(long id) throws RuntimeException {
        return id;
    }

    Resp<E> get(long id) throws RuntimeException;

    Resp<E> save(E entity) throws RuntimeException;

    Resp<E> update(long id, E entity) throws RuntimeException;

    Resp<Long> delete(long id) throws RuntimeException;

    Resp<List<E>> find() throws RuntimeException;

    Resp<PageDTO<E>> paging(int pageNumber, int pageSize) throws RuntimeException;

    Resp<PageDTO<E>> paging(int pageNumber, int pageSize, Sort sort) throws RuntimeException;

}
