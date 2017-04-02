package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.service.RoleService;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.controller.CRUDController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "角色管理",description = "角色信息管理")
@RequestMapping(value = "/auth/manage/role/")
public class RoleController implements CRUDController<RoleService, Role> {

    @Autowired
    private RoleService roleService;

    @Override
    public RoleService getDewService() {
        return roleService;
    }

    @GetMapping(value = "tenant/{tenantCode}")
    public Resp<List<Role>> findByTenantCode(@PathVariable String tenantCode) {
        return roleService.findByTenantCode(tenantCode);
    }

}
