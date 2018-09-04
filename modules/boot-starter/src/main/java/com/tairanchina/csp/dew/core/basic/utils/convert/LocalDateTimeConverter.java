package com.tairanchina.csp.dew.core.basic.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String str) {
        if (StringUtils.isEmpty(str)){
            return null;
        }
        if (str.matches("[1-9][0-9]+")){
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(str)), ZoneId.systemDefault());
        }
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
