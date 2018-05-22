package com.tairanchina.csp.dew.example.sleuth.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.DynamicConverter;


/**
 * desription: 这里需要继承DynamicConverter
 * Created by ding on 2018/1/23.
 */
public class TestConverter extends DynamicConverter<ILoggingEvent> {

    @Override
    public String convert(ILoggingEvent event) {
        return event.getMessage();
    }
}
