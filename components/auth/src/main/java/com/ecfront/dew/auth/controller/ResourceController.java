package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.service.ResourceService;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.controller.CRUDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth/manage/resource/")
public class ResourceController implements CRUDController<ResourceService, Resource> {

    @Autowired
    private ResourceService resourceService;

    @Override
    public ResourceService getDewService() {
        return resourceService;
    }

    @GetMapping(value = "tenant/{tenantCode}")
    public Resp<Map<String, List<Resource>>> findByTenantCodeGroupByCategory(@PathVariable String tenantCode) {
        return resourceService.findByTenantCodeGroupByCategory(tenantCode);
    }

}
