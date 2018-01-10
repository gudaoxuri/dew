package com.ecfront.dew.jdbc.test.select.dao;


import com.ecfront.dew.jdbc.test.select.dto.ModelDTO;
import com.ecfront.dew.jdbc.test.select.entity.SystemConfig;
import com.ecfront.dew.jdbc.DewDao;
import com.ecfront.dew.jdbc.annotations.Param;
import com.ecfront.dew.jdbc.annotations.Select;
import com.ecfront.dew.jdbc.test.select.dto.ModelDTO;
import com.ecfront.dew.jdbc.test.select.entity.SystemConfig;


public interface SystemConfigDao extends DewDao<String ,SystemConfig> {

    @Select(value = "SELECT s.*,t.*" +
            "FROM system_config s " +
            "INNER JOIN test_select_entity t ON s.create_time = t.create_time " +
            "WHERE s.value = #{value}",entityClass = ModelDTO.class)
    ModelDTO testLink(@Param("value") String value);
}
