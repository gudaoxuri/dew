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

    private Class<E> modelClazz = null;
    private boolean isStatusEntity = false;

    {
        modelClazz = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        isStatusEntity = StatusEntity.class.isAssignableFrom(modelClazz) || SafeStatusEntity.class.isAssignableFrom(modelClazz);
    }

    @Autowired
    private T dewRepository;

    @Override
    public Resp<E> getById(long id) throws RuntimeException {
        logger.debug("[{}] GetById:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preGetById(id);
        if (preResult.ok()) {
            return Resp.success(postGet(dewRepository.getById(id)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<E> getByCode(String code) throws RuntimeException {
        logger.debug("[{}] GetByCode:{}.", modelClazz.getSimpleName(), code);
        Resp<Boolean> preResult = preGetByCode(code);
        if (preResult.ok()) {
            return Resp.success(postGet(dewRepository.getByCode(code)));
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
    public Resp<Void> enableById(long id) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] EnableById:{}.", modelClazz.getSimpleName(), id);
            Resp<Boolean> preResult = preEnableById(id);
            if (preResult.ok()) {
                dewRepository.enableById(id);
                postEnableById(id);
                return Resp.success(null);
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    @Transactional
    public Resp<Void> enableByCode(String code) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] EnableByCode:{}.", modelClazz.getSimpleName(), code);
            Resp<Boolean> preResult = preEnableByCode(code);
            if (preResult.ok()) {
                dewRepository.enableByCode(code);
                postEnableByCode(code);
                return Resp.success(null);
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    @Transactional
    public Resp<Void> disableById(long id) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] DisableById:{}.", modelClazz.getSimpleName(), id);
            Resp<Boolean> preResult = preDisableById(id);
            if (preResult.ok()) {
                dewRepository.disableById(id);
                postDisableById(id);
                return Resp.success(null);
            }
            return Resp.customFail(preResult.getCode(), preResult.getMessage());
        } else {
            return Resp.notFound(modelClazz.getSimpleName() + "is not Status Entity");
        }
    }

    @Override
    @Transactional
    public Resp<Void> disableByCode(String code) throws RuntimeException {
        if (isStatusEntity) {
            logger.debug("[{}] DisableByCode:{}.", modelClazz.getSimpleName(), code);
            Resp<Boolean> preResult = preDisableByCode(code);
            if (preResult.ok()) {
                dewRepository.disableByCode(code);
                postDisableByCode(code);
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
            return Resp.success(postSave(dewRepository.save(entity)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    @Transactional
    public Resp<E> updateById(long id, E entity) throws RuntimeException {
        logger.debug("[{}] UpdateById:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preUpdateById(id, entity);
        if (preResult.ok()) {
            return Resp.success(postUpdate(dewRepository.updateById(id, entity)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    @Transactional
    public Resp<E> updateByCode(String code, E entity) throws RuntimeException {
        logger.debug("[{}] UpdateByCode:{}.", modelClazz.getSimpleName(), code);
        Resp<Boolean> preResult = preUpdateByCode(code, entity);
        if (preResult.ok()) {
            return Resp.success(postUpdate(dewRepository.updateByCode(code, entity)));
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    @Transactional
    public Resp<Void> deleteById(long id) throws RuntimeException {
        logger.debug("[{}] DeleteById:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preDeleteById(id);
        if (preResult.ok()) {
            dewRepository.deleteById(id);
            postDeleteById(id);
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    @Transactional
    public Resp<Void> deleteByCode(String code) throws RuntimeException {
        logger.debug("[{}] DeleteByCode:{}.", modelClazz.getSimpleName(), code);
        Resp<Boolean> preResult = preDeleteByCode(code);
        if (preResult.ok()) {
            dewRepository.deleteByCode(code);
            postDeleteByCode(code);
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<Boolean> existById(long id) throws RuntimeException {
        logger.debug("[{}] ExistById:{}.", modelClazz.getSimpleName(), id);
        Resp<Boolean> preResult = preExistById(id);
        if (preResult.ok()) {
            boolean result = dewRepository.existById(id);
            postExistById(id);
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Override
    public Resp<Boolean> existByCode(String code) throws RuntimeException {
        logger.debug("[{}] ExistByCode:{}.", modelClazz.getSimpleName(), code);
        Resp<Boolean> preResult = preExistByCode(code);
        if (preResult.ok()) {
            boolean result = dewRepository.existByCode(code);
            postExistByCode(code);
            return Resp.success(result);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}

