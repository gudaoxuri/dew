package com.ecfront.dew.core.log;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.OptInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Optional;

public class DewLogger implements Logger {

    private Logger logger;

    public static Logger getLogger(Class<?> clazz) {
        return new DewLogger(clazz);
    }

    public DewLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz.getName());
    }

    private String mixInfo(String msg) {
        Optional<OptInfo> optInfo = Dew.context().optInfo();
        if (optInfo.isPresent()) {
            return optInfo.get().getAccountCode() + "# " + msg;
        } else {
            return "# " + msg;
        }
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        logger.trace(mixInfo(s));
    }

    @Override
    public void trace(String s, Object o) {
        logger.trace(mixInfo(s), o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        logger.trace(mixInfo(s), o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        logger.trace(mixInfo(s), objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logger.trace(mixInfo(s), throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {
        logger.trace(marker, mixInfo(s));
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        logger.trace(marker, mixInfo(s), o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        logger.trace(marker, mixInfo(s), o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        logger.trace(marker, mixInfo(s), objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        logger.trace(marker, mixInfo(s), throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        logger.debug(mixInfo(s));
    }

    @Override
    public void debug(String s, Object o) {
        logger.debug(mixInfo(s), o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        logger.debug(mixInfo(s), o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        logger.debug(mixInfo(s), objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        logger.debug(mixInfo(s), throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {
        logger.debug(marker, mixInfo(s));
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        logger.debug(marker, mixInfo(s), o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        logger.debug(marker, mixInfo(s), o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        logger.debug(marker, mixInfo(s), objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        logger.debug(marker, mixInfo(s), throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        logger.info(mixInfo(s));
    }

    @Override
    public void info(String s, Object o) {
        logger.info(mixInfo(s), o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        logger.info(mixInfo(s), o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        logger.info(mixInfo(s), objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        logger.info(mixInfo(s), throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String s) {
        logger.info(marker, mixInfo(s));
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        logger.info(marker, mixInfo(s), o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        logger.info(marker, mixInfo(s), o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        logger.info(marker, mixInfo(s), objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        logger.info(marker, mixInfo(s), throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        logger.warn(mixInfo(s));
    }

    @Override
    public void warn(String s, Object o) {
        logger.warn(s, mixInfo(s));
    }

    @Override
    public void warn(String s, Object... objects) {
        logger.warn(s, mixInfo(s), objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        logger.warn(s, mixInfo(s), o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        logger.warn(mixInfo(s), throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {
        logger.warn(marker, mixInfo(s));
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        logger.warn(marker, mixInfo(s), o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        logger.warn(marker, mixInfo(s), o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        logger.warn(marker, mixInfo(s), objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        logger.warn(marker, mixInfo(s), throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        logger.error(mixInfo(s));
    }

    @Override
    public void error(String s, Object o) {
        logger.error(mixInfo(s), o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        logger.error(mixInfo(s), o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        logger.error(mixInfo(s), objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        logger.error(mixInfo(s), throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {
        logger.error(marker, mixInfo(s));
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        logger.error(marker, mixInfo(s), o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        logger.error(marker, mixInfo(s), o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        logger.error(marker, mixInfo(s), objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        logger.error(marker, mixInfo(s), throwable);
    }

}
