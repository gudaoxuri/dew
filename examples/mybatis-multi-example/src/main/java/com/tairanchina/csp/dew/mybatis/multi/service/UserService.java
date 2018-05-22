package com.tairanchina.csp.dew.mybatis.multi.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tairanchina.csp.dew.mybatis.multi.entity.User;
import com.tairanchina.csp.dew.mybatis.multi.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {


    public List<String> ageGroup() {
        return baseMapper.ageGroup();
    }

    public void batchInsert(List<User> list){
        baseMapper.batchInsert(list);
    }
}
