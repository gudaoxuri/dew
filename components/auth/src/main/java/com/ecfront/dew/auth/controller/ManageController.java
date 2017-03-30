package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.core.controller.SimpleController;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/manage")
public class ManageController extends SimpleController<SimpleServiceImpl, Tenant> {

    private static final Logger logger = LoggerFactory.getLogger(ManageController.class);

}
