package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class SimpleServiceImpl<T extends DewRepository<E, ID>, E extends IdEntity, ID extends Serializable> implements SimpleService<T, E, ID> {

    protected static final Logger logger = LoggerFactory.getLogger(SimpleServiceImpl.class);

    protected Class<E> modelClazz = (Class<E>) ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];

    @Autowired
    public T dewRepository;

    @Override
    public Resp<E> get(ID id) throws RuntimeException {
        logger.debug("[{}] Get:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preGet(id);
        if (preResult.ok()) {
            return Resp.success(postGet(dewRepository.findOne(id)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<ID> delete(ID id) throws RuntimeException {
        logger.debug("[{}] Delete:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preDelete(id);
        if (preResult.ok()) {
            dewRepository.delete(id);
            return Resp.success(postDelete(id));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<List<E>> find() throws RuntimeException {
        logger.debug("[{}] Find.", modelClazz.getSimpleName());
        Resp<Boolean> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(dewRepository.findAll());
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<PageDTO<E>> paging(int pageNumber, int pageSize) throws RuntimeException {
        logger.debug("[{}] Paging {} {}.", modelClazz.getSimpleName(), pageNumber, pageSize);
        Resp<Boolean> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(dewRepository.paging(pageNumber, pageSize, null));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<PageDTO<E>> paging(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] Paging {} {} {}.", modelClazz.getSimpleName(), pageNumber, pageSize, sort.toString());
        Resp<Boolean> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(dewRepository.paging(pageNumber, pageSize, sort));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<E> update(ID id, E entity) throws RuntimeException {
        logger.debug("[{}] Update {}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preUpdate(id, entity);
        if (preResult.ok()) {
            return Resp.success(dewRepository.update(id, entity));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<E> save(E entity) throws RuntimeException {
        logger.debug("[{}] Save.", modelClazz.getSimpleName());
        Resp<Boolean> preResult = preSave(entity);
        if (preResult.ok()) {
            return Resp.success(dewRepository.save(entity));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }
}

