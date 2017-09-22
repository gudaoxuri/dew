package com.ecfront.dew.core.test.crud.service;

import com.ecfront.dew.core.test.dataaccess.select.dao.TestInterfaceDao;
import com.ecfront.dew.core.test.crud.entity.TestSelectEntity;
import com.ecfront.dew.core.service.CRUDSService;
import org.springframework.stereotype.Service;

@Service
public class CRUDSTestService implements CRUDSService<TestInterfaceDao, Integer, TestSelectEntity> {

}
