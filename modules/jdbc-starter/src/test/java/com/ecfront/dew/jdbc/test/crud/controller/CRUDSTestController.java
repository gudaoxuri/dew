package com.ecfront.dew.jdbc.test.crud.controller;


import com.ecfront.dew.jdbc.test.crud.service.CRUDSTestService;
import com.ecfront.dew.core.controller.CRUDSController;
import com.ecfront.dew.jdbc.test.crud.entity.TestSelectEntity;
import com.ecfront.dew.jdbc.test.crud.service.CRUDSTestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crud/")
public class CRUDSTestController implements CRUDSController<CRUDSTestService,Integer, TestSelectEntity> {

    @Override
    public boolean convertAble() {
        return true;
    }


}
