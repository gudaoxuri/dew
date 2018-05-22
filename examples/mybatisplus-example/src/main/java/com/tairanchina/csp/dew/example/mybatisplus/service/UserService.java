package com.tairanchina.csp.dew.example.mybatisplus.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tairanchina.csp.dew.example.mybatisplus.entity.User;
import com.tairanchina.csp.dew.example.mybatisplus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    public List<String> ageGroup(){
        return baseMapper.ageGroup();
    }

}
