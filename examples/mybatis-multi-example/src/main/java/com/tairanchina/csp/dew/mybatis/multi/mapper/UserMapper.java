package com.tairanchina.csp.dew.mybatis.multi.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tairanchina.csp.dew.jdbc.annotations.Param;
import com.tairanchina.csp.dew.jdbc.mybatis.annotion.DS;
import com.tairanchina.csp.dew.mybatis.multi.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@DS
public interface UserMapper extends BaseMapper<User> {

    @Select("select age from user group by age")
    List<String> ageGroup();

    void batchInsert(@Param("users") List<User> users);
}
