package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.entity.SafeStatusEntity;
import com.ecfront.dew.core.entity.StatusEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class SimpleServiceImpl<T extends DewRepository<E>, E extends IdEntity> implements SimpleService<T, E> {

    protected static final Logger logger = LoggerFactory.getLogger(SimpleServiceImpl.class);

    protected Class<E> modelClazz = null;
    protected boolean isStatusEntity = false;

    {
        modelClazz = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        isStatusEntity = StatusEntity.class.isAssignableFrom(modelClazz) || SafeStatusEntity.class.isAssignableFrom(modelClazz);
    }

    @Autowired
    public T dewRepository;

    @Override
    public Resp<E> get(long id) throws RuntimeException {
        logger.debug("[{}] Get:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preGet(id);
        if (preResult.ok()) {
            return Resp.success(postGet(dewRepository.findOne(id)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<List<E>> find() throws RuntimeException {
        logger.debug("[{}] Find.", modelClazz.getSimpleName());
        Resp<Boolean> preResult = preFind();
        if (preResult.ok()) {
            return Resp.success(postFind(dewRepository.findAll()));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<List<E>> findEnable() throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] FindEnable.", modelClazz.getSimpleName());
            Resp<Boolean> preResult = preFind();
            if (preResult.ok()) {
                return Resp.success(postFind(dewRepository.findEnable()));
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    public Resp<List<E>> findDisable() throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] FindDisable.", modelClazz.getSimpleName());
            Resp<Boolean> preResult = preFind();
            if (preResult.ok()) {
                return Resp.success(postFind(dewRepository.findDisable()));
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    public Resp<PageDTO<E>> paging(int pageNumber, int pageSize) throws RuntimeException {
        return paging(pageNumber, pageSize, null);
    }

    @Override
    public Resp<PageDTO<E>> paging(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        logger.debug("[{}] Paging {} {} {}.", modelClazz.getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
        Resp<Boolean> preResult = prePaging();
        if (preResult.ok()) {
            return Resp.success(postPaging(dewRepository.paging(pageNumber, pageSize, sort)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize) throws RuntimeException {
        return pagingEnable(pageNumber, pageSize, null);
    }

    @Override
    public Resp<PageDTO<E>> pagingEnable(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] PagingEnable {} {} {}.", modelClazz.getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
            Resp<Boolean> preResult = prePaging();
            if (preResult.ok()) {
                return Resp.success(postPaging(dewRepository.pagingEnable(pageNumber, pageSize, sort)));
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    public Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize) throws RuntimeException {
        return pagingDisable(pageNumber, pageSize, null);
    }

    @Override
    public Resp<PageDTO<E>> pagingDisable(int pageNumber, int pageSize, Sort sort) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] PagingDisable {} {} {}.", modelClazz.getSimpleName(), pageNumber, pageSize, sort != null ? sort.toString() : "");
            Resp<Boolean> preResult = prePaging();
            if (preResult.ok()) {
                return Resp.success(postPaging(dewRepository.pagingDisable(pageNumber, pageSize, sort)));
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    @Transactional
    public Resp<Void> enable(long id) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] Enable:{}.", modelClazz.getSimpleName(), id);
            Resp<Boolean> preResult = preEnable(id);
            if (preResult.ok()) {
                dewRepository.enable(id);
                postEnable(id);
                return Resp.success(null);
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    @Transactional
    public Resp<Void> disable(long id) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] Disable:{}.", modelClazz.getSimpleName(), id);
            Resp<Boolean> preResult = preDisable(id);
            if (preResult.ok()) {
                dewRepository.disable(id);
                postDisable(id);
                return Resp.success(null);
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    @Transactional
    public Resp<E> save(E entity) throws RuntimeException {
        logger.debug("[{}] Save.", modelClazz.getSimpleName());
        Resp<Boolean> preResult = preSave(entity);
        if (preResult.ok()) {
            return Resp.success(dewRepository.save(entity));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    @Transactional
    public Resp<E> update(long id, E entity) throws RuntimeException {
        logger.debug("[{}] Update {}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preUpdate(id, entity);
        if (preResult.ok()) {
            return Resp.success(dewRepository.update(id, entity));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    @Transactional
    public Resp<Long> delete(long id) throws RuntimeException {
        logger.debug("[{}] Delete:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preDelete(id);
        if (preResult.ok()) {
            dewRepository.delete(id);
            return Resp.success(postDelete(id));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }
}

