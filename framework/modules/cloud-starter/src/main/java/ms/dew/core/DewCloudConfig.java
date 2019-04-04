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

package ms.dew.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * Dew cloud config.
 *
 * @author gudaoxuri
 */
@ConfigurationProperties(prefix = "dew.cloud")
public class DewCloudConfig {

    private TraceLog traceLog = new TraceLog();
    private Error error = new Error();

    /**
     * Gets trace log.
     *
     * @return the trace log
     */
    public TraceLog getTraceLog() {
        return traceLog;
    }

    /**
     * Sets trace log.
     *
     * @param traceLog the trace log
     */
    public void setTraceLog(TraceLog traceLog) {
        this.traceLog = traceLog;
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public Error getError() {
        return error;
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(Error error) {
        this.error = error;
    }

    /**
     * Trace log config.
     */
    public static class TraceLog {

        private boolean enabled = true;

        /**
         * Is enabled.
         *
         * @return the boolean
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets enabled.
         *
         * @param enabled the enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Hystrix Error notify config.
     */
    public static class Error {

        private boolean enabled = false;
        private String notifyFlag = "__HYSTRIX__";
        private String notifyTitle = "服务异常";
        private String[] notifyIncludeKeys = new String[]{};
        private String[] notifyExcludeKeys = new String[]{};
        private Set<String> notifyEventTypes = new HashSet<String>() {
            {
                add("FAILURE");
                add("SHORT_CIRCUITED");
                add("TIMEOUT");
                add("THREAD_POOL_REJECTED");
                add("SEMAPHORE_REJECTED");
            }
        };

        /**
         * Is enabled.
         *
         * @return the boolean
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets enabled.
         *
         * @param enabled the enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Gets notify flag.
         *
         * @return the notify flag
         */
        public String getNotifyFlag() {
            return notifyFlag;
        }

        /**
         * Sets notify flag.
         *
         * @param notifyFlag the notify flag
         */
        public void setNotifyFlag(String notifyFlag) {
            this.notifyFlag = notifyFlag;
        }

        /**
         * Gets notify title.
         *
         * @return the notify title
         */
        public String getNotifyTitle() {
            return notifyTitle;
        }

        /**
         * Sets notify title.
         *
         * @param notifyTitle the notify title
         */
        public void setNotifyTitle(String notifyTitle) {
            this.notifyTitle = notifyTitle;
        }

        /**
         * Gets notify event types.
         *
         * @return the notify event types
         */
        public Set<String> getNotifyEventTypes() {
            return notifyEventTypes;
        }

        /**
         * Sets notify event types.
         *
         * @param notifyEventTypes the notify event types
         */
        public void setNotifyEventTypes(Set<String> notifyEventTypes) {
            this.notifyEventTypes = notifyEventTypes;
        }

        /**
         * Get notify include keys.
         *
         * @return the string [ ]
         */
        public String[] getNotifyIncludeKeys() {
            return notifyIncludeKeys;
        }

        /**
         * Sets notify include keys.
         *
         * @param notifyIncludeKeys the notify include keys
         */
        public void setNotifyIncludeKeys(String[] notifyIncludeKeys) {
            this.notifyIncludeKeys = notifyIncludeKeys;
        }

        /**
         * Get notify exclude keys.
         *
         * @return the string [ ]
         */
        public String[] getNotifyExcludeKeys() {
            return notifyExcludeKeys;
        }

        /**
         * Sets notify exclude keys.
         *
         * @param notifyExcludeKeys the notify exclude keys
         */
        public void setNotifyExcludeKeys(String[] notifyExcludeKeys) {
            this.notifyExcludeKeys = notifyExcludeKeys;
        }
    }
}
