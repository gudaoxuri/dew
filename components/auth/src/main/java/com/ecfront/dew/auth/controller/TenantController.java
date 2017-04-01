package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.service.TenantService;
import com.ecfront.dew.core.controller.CRUSController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("租户管理")
@RequestMapping(value = "/auth/manage/tenant/")
public class TenantController implements CRUSController<TenantService, Tenant> {

    @Autowired
    private TenantService tenantService;

    @Override
    public TenantService getDewService() {
        return tenantService;
    }

}
