package com.tairanchina.csp.dew.mybatis.multi.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tairanchina.csp.dew.jdbc.mybatis.annotion.DS;
import com.tairanchina.csp.dew.mybatis.multi.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@DS(dataSource = "test1")
public interface UserMapper2 extends BaseMapper<User> {

    @Select("select age from user group by age")
    List<String> ageGroup();

}
