package com.ecfront.dew.jdbc.test.crud.service;


import com.ecfront.dew.jdbc.test.select.dao.TestInterfaceDao;
import com.ecfront.dew.core.service.CRUDSService;
import com.ecfront.dew.jdbc.test.crud.entity.TestSelectEntity;
import com.ecfront.dew.jdbc.test.select.dao.TestInterfaceDao;
import org.springframework.stereotype.Service;

@Service
public class CRUDSTestService implements CRUDSService<TestInterfaceDao, Integer, TestSelectEntity> {

}
