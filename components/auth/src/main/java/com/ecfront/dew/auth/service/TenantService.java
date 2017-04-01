package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.repository.TenantRepository;
import com.ecfront.dew.common.Resp;
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
    public Resp<Optional<Object>> preEnableById(long id) throws RuntimeException {
        return null;
    }

    @Override
    public Resp<Optional<Object>> preEnableByCode(String code) throws RuntimeException {
        return null;
    }

    @Override
    public void postEnableById(long id, Optional<Object> preBody) throws RuntimeException {

    }

    @Override
    public void postEnableByCode(String code, Optional<Object> preBody) throws RuntimeException {

    }

    @Override
    public void postDisableById(long id, Optional<Object> preBody) throws RuntimeException {

    }

    @Override
    public void postDisableByCode(String code, Optional<Object> preBody) throws RuntimeException {

    }

    @Override
    public Tenant postSave(Tenant entity, Optional<Object> preBody) throws RuntimeException {
        return null;
    }

    @Override
    public Tenant postUpdate(Tenant entity, Optional<Object> preBody) throws RuntimeException {
        return null;
    }
}
