package com.trc.dew.crud.service;


import com.tairanchina.csp.dew.core.service.CRUDSService;
import com.trc.dew.crud.entity.TestSelectEntity;
import com.trc.dew.select.dao.TestInterfaceDao;
import org.springframework.stereotype.Service;

@Service
public class CRUDSTestService implements CRUDSService<TestInterfaceDao, Integer, TestSelectEntity> {

}
