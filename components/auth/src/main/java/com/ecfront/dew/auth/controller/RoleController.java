package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.service.RoleService;
import com.ecfront.dew.core.controller.SimpleController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/manage/role/")
public class RoleController extends SimpleController<RoleService, Role> {


}
