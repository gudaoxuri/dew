package com.tairanchina.csp.dew.core.basic.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.Instant;

public class InstantConvert implements Converter<String, Instant> {
    @Override
    public Instant convert(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return Instant.ofEpochMilli(Long.valueOf(str));
    }
}
