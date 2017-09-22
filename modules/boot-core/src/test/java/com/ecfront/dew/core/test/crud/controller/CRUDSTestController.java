package com.ecfront.dew.core.test.crud.controller;

import com.ecfront.dew.core.test.crud.service.CRUDSTestService;
import com.ecfront.dew.core.controller.CRUDSController;
import com.ecfront.dew.core.test.crud.entity.TestSelectEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crud/")
public class CRUDSTestController implements CRUDSController<CRUDSTestService,Integer, TestSelectEntity> {

}
