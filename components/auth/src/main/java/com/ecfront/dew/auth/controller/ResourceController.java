package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.service.ResourceService;
import com.ecfront.dew.core.controller.SimpleController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/manage/resource/")
public class ResourceController extends SimpleController<ResourceService, Resource> {


}
