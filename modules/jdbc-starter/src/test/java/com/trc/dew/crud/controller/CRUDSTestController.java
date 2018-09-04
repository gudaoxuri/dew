package com.trc.dew.crud.controller;


import com.trc.dew.crud.entity.TestSelectEntity;
import com.trc.dew.crud.service.CRUDSTestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crud")
public class CRUDSTestController implements CRUDSController<CRUDSTestService,Integer, TestSelectEntity> {

    @Override
    public boolean convertAble() {
        return true;
    }


}
