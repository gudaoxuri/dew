package com.ecfront.dew.core.test.crud.service;

import com.ecfront.dew.core.service.CRUDSService;
import com.ecfront.dew.core.test.crud.entity.TestSelectEntity;
import org.springframework.stereotype.Service;

@Service
public class CRUDSTestService implements CRUDSService<TestSelectEntity.ActiveRecord, Integer, TestSelectEntity> {

}
