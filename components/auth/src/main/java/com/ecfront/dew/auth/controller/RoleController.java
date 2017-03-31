package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.service.RoleService;
import com.ecfront.dew.core.controller.CRUDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/manage/role/")
public class RoleController implements CRUDController<RoleService, Role> {

    @Autowired
    private RoleService roleService;

    @Override
    public RoleService getDewService() {
        return roleService;
    }

}
