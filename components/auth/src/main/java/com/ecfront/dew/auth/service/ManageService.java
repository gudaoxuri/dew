package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.repository.TenantRepository;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ManageService extends SimpleServiceImpl<TenantRepository, Tenant, String> {

    private static final Logger logger = LoggerFactory.getLogger(ManageService.class);

}
