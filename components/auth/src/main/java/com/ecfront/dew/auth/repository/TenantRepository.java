package com.ecfront.dew.auth.repository;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.core.repository.DewRepository;

import javax.annotation.Resource;

@Resource
public interface TenantRepository extends DewRepository<Tenant, String> {

}
