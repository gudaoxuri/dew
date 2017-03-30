package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.repository.TenantRepository;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TenantService extends SimpleServiceImpl<TenantRepository, Tenant> {

}
