/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.devops.kernel.util;

import org.apache.maven.plugin.logging.Log;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;

import static org.fusesource.jansi.Ansi.Color.*;

/**
 * Dew log.
 *
 * @author gudaoxuri
 */
public class DewLog implements Log {

    private Logger log;
    private String prefix;

    /**
     * Instantiates a new Dew log.
     *
     * @param log    the log
     * @param prefix the prefix
     */
    public DewLog(Logger log, String prefix) {
        this.log = log;
        this.prefix = prefix;
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(CharSequence content) {
        log.debug(ansi(content.toString(), WHITE));
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        log.debug(ansi(content.toString(), WHITE, error));
    }

    @Override
    public void debug(Throwable error) {
        log.debug(ansi("", WHITE, error));
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(CharSequence content) {
        log.info(ansi(content.toString(), GREEN));
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        log.info(ansi(content.toString(), GREEN, error));
    }

    @Override
    public void info(Throwable error) {
        log.info(ansi("", GREEN, error));
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(CharSequence content) {
        log.warn(ansi(content.toString(), YELLOW));
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        log.warn(ansi(content.toString(), YELLOW, error));
    }

    @Override
    public void warn(Throwable error) {
        log.warn(ansi("", YELLOW, error));
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(CharSequence content) {
        log.error(ansi(content.toString(), RED));
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        log.error(ansi(content.toString(), RED, error));
    }

    @Override
    public void error(Throwable error) {
        log.error(ansi("", RED, error));
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
