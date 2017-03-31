package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.repository.TenantRepository;
import com.ecfront.dew.core.service.CRUSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
