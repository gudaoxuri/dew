/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.devops.kernel.util;

import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import static org.fusesource.jansi.Ansi.Color.*;

/**
 * Dew log.
 *
 * @author gudaoxuri
 */
public class DewLog implements Logger {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    private Logger log;
    private String prefix;

    /**
     * Build logger.
     *
     * @param clazz the clazz
     * @return the logger
     */
    public static Logger build(Class clazz) {
        String context = CONTEXT.get();
        if (context == null) {
            context = "";
        }
        return new DewLog(LoggerFactory.getLogger(clazz), "[DEW]" + context);
    }

    public static Logger build(Class clazz, String appName) {
        CONTEXT.set(String.format("> [%-20s]", appName));
        return new DewLog(LoggerFactory.getLogger(clazz), "[DEW]" + CONTEXT.get());
    }

    /**
     * Instantiates a new Dew log.
     *
     * @param log    the log
     * @param prefix the prefix
     */
    private DewLog(Logger log, String prefix) {
        this.log = log;
        this.prefix = prefix;
    }

    @Override
    public String getName() {
        return log.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker);
    }

    @Override
    public void trace(String msg) {
        log.trace(ansi(msg, DEFAULT));
    }

    @Override
    public void trace(String format, Object arg) {
        log.trace(ansi(format, DEFAULT, arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log.trace(ansi(format, DEFAULT, arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        log.trace(ansi(format, DEFAULT, arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        log.trace(ansi(msg, DEFAULT, t));
    }

    @Override
    public void trace(Marker marker, String msg) {
        log.trace(marker, ansi(msg, DEFAULT));
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        log.trace(marker, ansi(format, DEFAULT, arg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        log.trace(marker, ansi(format, DEFAULT, arg1, arg2));
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        log.trace(marker, ansi(format, DEFAULT, argArray));
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        log.trace(marker, ansi(msg, DEFAULT, t));
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker);
    }

    @Override
    public void debug(String msg) {
        log.debug(ansi(msg, CYAN));
    }

    @Override
    public void debug(String format, Object arg) {
        log.debug(ansi(format, CYAN, arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log.debug(ansi(format, CYAN, arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        log.debug(ansi(format, CYAN, arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        log.debug(ansi(msg, CYAN, t));
    }

    @Override
    public void debug(Marker marker, String msg) {
        log.debug(marker, ansi(msg, CYAN));
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        log.debug(marker, ansi(format, CYAN, arg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        log.debug(marker, ansi(format, CYAN, arg1, arg2));
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        log.debug(marker, ansi(format, CYAN, arguments));
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        log.debug(marker, ansi(msg, CYAN, t));
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker);
    }

    @Override
    public void info(String msg) {
        log.info(ansi(msg, BLUE));
    }

    @Override
    public void info(String format, Object arg) {
        log.info(ansi(format, BLUE, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log.info(ansi(format, BLUE, arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        log.info(ansi(format, BLUE, arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        log.info(ansi(msg, BLUE, t));
    }

    @Override
    public void info(Marker marker, String msg) {
        log.info(marker, ansi(msg, BLUE));
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        log.info(marker, ansi(format, BLUE, arg));
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        log.info(marker, ansi(format, BLUE, arg1, arg2));
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        log.info(marker, ansi(format, BLUE, arguments));
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        log.info(marker, ansi(msg, BLUE, t));
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker);
    }

    @Override
    public void warn(String msg) {
        log.warn(ansi(msg, YELLOW));
    }

    @Override
    public void warn(String format, Object arg) {
        log.warn(ansi(format, YELLOW, arg));
    }

    @Override
    public void warn(String format, Object... arguments) {
        log.warn(ansi(format, YELLOW, arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log.warn(ansi(format, YELLOW, arg1, arg2));
    }

    @Override
    public void warn(String msg, Throwable t) {
        log.warn(ansi(msg, YELLOW, t));
    }

    @Override
    public void warn(Marker marker, String msg) {
        log.warn(marker, ansi(msg, YELLOW));
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        log.warn(marker, ansi(format, YELLOW, arg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        log.warn(marker, ansi(format, YELLOW, arg1, arg2));
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        log.warn(marker, ansi(format, YELLOW, arguments));
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        log.warn(marker, ansi(msg, YELLOW, t));
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker);
    }

    @Override
    public void error(String msg) {
        log.error(ansi(msg, RED));
    }

    @Override
    public void error(String format, Object arg) {
        log.error(ansi(format, RED, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log.error(ansi(format, RED, arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        log.error(ansi(format, RED, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        log.error(ansi(msg, RED, t));
    }

    @Override
    public void error(Marker marker, String msg) {
        log.error(marker, ansi(msg, RED));
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        log.error(marker, ansi(format, RED, arg));
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, ansi(format, RED, arg1, arg2));
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        log.error(marker, ansi(format, RED, arguments));
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        log.error(marker, ansi(msg, RED, t));
    }

    private String ansi(String message, Ansi.Color color, Object... params) {
        return Ansi.ansi().fg(color).a(format(prefix + message, params)).reset().toString();
    }

    private String format(String message, Object[] params) {
        if (params.length == 0) {
            return message;
        } else if (params.length == 1 && params[0] instanceof Throwable) {
            return message + ": " + params[0].toString();
        } else {
            return String.format(message, params);
        }
    }

}
