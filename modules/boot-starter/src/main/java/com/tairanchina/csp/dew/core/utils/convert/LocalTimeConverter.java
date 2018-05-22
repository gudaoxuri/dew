package com.tairanchina.csp.dew.core.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * desription:
 * Created by ding on 2017/10/31.
 */
public class LocalTimeConverter implements Converter<String,LocalTime> {

    @Override
    public LocalTime convert(String str) {
        if (StringUtils.isEmpty(str)){
            return null;
        }
        return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

}
