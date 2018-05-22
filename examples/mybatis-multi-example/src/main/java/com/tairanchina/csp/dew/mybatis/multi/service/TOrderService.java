package com.tairanchina.csp.dew.mybatis.multi.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tairanchina.csp.dew.jdbc.annotations.Param;
import com.tairanchina.csp.dew.mybatis.multi.entity.TOrder;
import com.tairanchina.csp.dew.mybatis.multi.mapper.TOrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * desription:
 * Created by ding on 2017/12/28.
 */
@Service
public class TOrderService extends ServiceImpl<TOrderMapper, TOrder> {

    public Long countAllByXml(){
        return baseMapper.countAllByXml();
    }

    public Integer updateByXml(TOrder tOrder){
        return baseMapper.updateByXml(tOrder);
    }

    public List<TOrder> getAllByXml(){
        return baseMapper.getAllByXml();
    }

    public TOrder getOneByXml(@Param("userId") Integer userId){
        return baseMapper.getOneByXml(userId);
    }

    public Integer insertByXml(TOrder tOrder){
        return baseMapper.insertByXml(tOrder);
    }

    public Integer deleteByXml(@Param("userId") Integer userId){
        return baseMapper.deleteByXml(userId);
    }
}
