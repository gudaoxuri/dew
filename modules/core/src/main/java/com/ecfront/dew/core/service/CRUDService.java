package com.ecfront.dew.core.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CRUDService<T extends DewRepository<E>, E extends IdEntity> extends CRUService<T, E> {

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

    @Transactional
    default Resp<Void> deleteById(long id) throws RuntimeException {
        logger.debug("[{}] DeleteById:{}.", getModelClazz().getSimpleName(), id);
        Resp<Boolean> preResult = preDeleteById(id);
        if (preResult.ok()) {
            getDewRepository().deleteById(id);
            postDeleteById(id);
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

    @Transactional
    default Resp<Void> deleteByCode(String code) throws RuntimeException {
        logger.debug("[{}] DeleteByCode:{}.", getModelClazz().getSimpleName(), code);
        Resp<Boolean> preResult = preDeleteByCode(code);
        if (preResult.ok()) {
            getDewRepository().deleteByCode(code);
            postDeleteByCode(code);
            return Resp.success(null);
        }
        return Resp.customFail(preResult.getCode(), preResult.getMessage());
    }

}
