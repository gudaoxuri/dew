package com.ecfront.dew.appexample.repository;

import com.ecfront.dew.appexample.entity.TestEntity;
import com.ecfront.dew.core.repository.DewRepository;

import javax.annotation.Resource;

@Resource
public interface TestRepository extends DewRepository<TestEntity> {

}
