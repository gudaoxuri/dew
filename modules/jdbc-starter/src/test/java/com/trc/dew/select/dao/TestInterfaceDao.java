package com.trc.dew.select.dao;


import com.ecfront.dew.common.Page;
import com.tairanchina.csp.dew.jdbc.DewDao;
import com.tairanchina.csp.dew.jdbc.annotations.ModelParam;
import com.tairanchina.csp.dew.jdbc.annotations.Param;
import com.tairanchina.csp.dew.jdbc.annotations.Select;
import com.trc.dew.crud.entity.TestSelectEntity;

import java.util.List;
import java.util.Map;


public interface TestInterfaceDao extends DewDao<Integer,TestSelectEntity> {


    @Select(value = "select * from `test_select_entity` where field_a= #{ fieldA }", entityClass = TestSelectEntity.class)
    Page<TestSelectEntity> queryByCustomPaging(@ModelParam TestSelectEntity model, @Param("pageNumber") Long pageNumber, @Param("pageSize") Integer pageSize);

    @Select(value = "select * from test_select_entity where field_a= #{ fieldA }", entityClass = TestSelectEntity.class)
    Page<TestSelectEntity> queryByDefaultPaging(@ModelParam TestSelectEntity model);

    @Select(value = "select * from test_select_entity where field_a= #{ fieldA }", entityClass = TestSelectEntity.class)
    List<TestSelectEntity> queryList(@ModelParam TestSelectEntity model);

    @Select(value = "select * from test_select_entity where field_a= #{ fieldA }", entityClass = TestSelectEntity.class)
    List<TestSelectEntity> queryByField(@Param("fieldA") String fieldA);

    @Select(value = "select * from test_select_entity where field_a= #{ fieldA } and field_c = #{fc}", entityClass = TestSelectEntity.class)
    List<TestSelectEntity> queryByTowFields(@Param("fieldA") String fieldA, @Param("fc") String f);

    @Select(value = "select * from test_select_entity where id= #{id}", entityClass = TestSelectEntity.class)
    TestSelectEntity getById(@Param("id") long id);

    @Select(value = "select * from test_select_entity where id= #{id}")
    Map<String,Object> getMapById(@Param("id") long id);

}
