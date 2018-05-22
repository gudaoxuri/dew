package com.tairanchina.csp.dew.mybatis.multi.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tairanchina.csp.dew.mybatis.multi.entity.User;
import com.tairanchina.csp.dew.mybatis.multi.mapper.UserMapper2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService2 extends ServiceImpl<UserMapper2, User> {


    public List<String> ageGroup() {
        return baseMapper.ageGroup();
    }

}
