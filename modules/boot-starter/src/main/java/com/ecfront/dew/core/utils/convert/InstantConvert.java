package com.ecfront.dew.core.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;

/**
 * desription:
 * Created by ding on 2017/11/5.
 */
@Component
public class InstantConvert implements Converter<String,Instant> {
    @Override
    public Instant convert(String str) {
        if (StringUtils.isEmpty(str)){
            return null;
        }
        return Instant.ofEpochMilli(Long.valueOf(str));
    }
}
