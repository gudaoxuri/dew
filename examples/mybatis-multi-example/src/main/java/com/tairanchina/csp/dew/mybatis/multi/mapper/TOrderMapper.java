package com.tairanchina.csp.dew.mybatis.multi.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tairanchina.csp.dew.jdbc.annotations.Param;
import com.tairanchina.csp.dew.jdbc.mybatis.annotion.DS;
import com.tairanchina.csp.dew.mybatis.multi.entity.TOrder;

import java.util.List;

/**
 * desription:
 * Created by ding on 2017/12/28.
 */
@DS(isSharding = true)
public interface TOrderMapper extends BaseMapper<TOrder> {

    Long countAllByXml();

    Integer updateByXml(TOrder tOrder);

    List<TOrder> getAllByXml();

    TOrder getOneByXml(@Param("userId") Integer userId);

    Integer insertByXml(TOrder tOrder);

    Integer deleteByXml(@Param("userId") Integer userId);
}
