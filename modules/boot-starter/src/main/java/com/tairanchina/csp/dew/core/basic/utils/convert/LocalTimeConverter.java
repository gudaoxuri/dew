package com.tairanchina.csp.dew.core.basic.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

}
