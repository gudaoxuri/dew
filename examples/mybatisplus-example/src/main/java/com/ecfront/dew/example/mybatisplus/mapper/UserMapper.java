package com.ecfront.dew.example.mybatisplus.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ecfront.dew.example.mybatisplus.entity.User;
import com.ecfront.dew.example.mybatisplus.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select age from user group by age")
    List<String> ageGroup();

}
