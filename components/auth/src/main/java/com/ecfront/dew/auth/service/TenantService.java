package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.repository.TenantRepository;
import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.service.CRUSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantService implements CRUSService<TenantRepository, Tenant> {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public Class<Tenant> getModelClazz() {
        return Tenant.class;
    }

    @Override
    public TenantRepository getDewRepository() {
        return tenantRepository;
    }

    @Override
    public void postEnableById(long id, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_TENANT_ADD, "", JsonHelper.toJsonString(getById(id).getBody()));
    }

    @Override
    public void postEnableByCode(String code, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_TENANT_ADD, "", JsonHelper.toJsonString(getByCode(code).getBody()));
    }

    @Override
    public void postDisableById(long id, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_TENANT_REMOVE, "", getById(id).getBody().getCode());
    }

    @Override
    public void postDisableByCode(String code, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_TENANT_REMOVE, "", code);
    }

    @Override
    public Tenant postSave(Tenant entity, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_TENANT_ADD, "", JsonHelper.toJsonString(entity));
        return entity;
    }

    @Override
    public Tenant postUpdate(Tenant entity, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_TENANT_ADD, "", JsonHelper.toJsonString(entity));
        return entity;
    }
}
