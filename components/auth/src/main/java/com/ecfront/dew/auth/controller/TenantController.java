package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.service.TenantService;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.controller.SimpleController;
import com.ecfront.dew.core.dto.PageDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/auth/manage/tenant/")
public class TenantController extends SimpleController<TenantService, Tenant> {

    @Override
    public Resp<List<Tenant>> find(Boolean enable) {
        return super.find(enable);
    }

    @Override
    public Resp<PageDTO<Tenant>> paging(@PathVariable int pageNumber, @PathVariable int pageSize, @RequestParam(required = false) Boolean enable) {
        return super.paging(pageNumber, pageSize, enable);
    }

}
