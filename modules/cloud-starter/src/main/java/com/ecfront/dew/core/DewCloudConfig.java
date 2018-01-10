package com.ecfront.dew.core;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "dew.cloud")
public class DewCloudConfig {

    private TraceLog traceLog = new TraceLog();
    private Error error = new Error();

    public static class TraceLog {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Error {

        private boolean enabled = false;

        private Set<String> notifyEmails = new HashSet<>();
        private String notifyTitle = "服务异常";
        private long notifyIntervalSec = 1800;
        private String[] notifyIncludeKeys = new String[]{};
        private String[] notifyExcludeKeys = new String[]{};
        private Set<String> notifyEventTypes = new HashSet<String>() {{
            add("FAILURE");
            add("SHORT_CIRCUITED");
            add("TIMEOUT");
            add("THREAD_POOL_REJECTED");
            add("SEMAPHORE_REJECTED");
        }};

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Set<String> getNotifyEmails() {
            return notifyEmails;
        }

        public void setNotifyEmails(Set<String> notifyEmails) {
            this.notifyEmails = notifyEmails;
        }

        public String getNotifyTitle() {
            return notifyTitle;
        }

        public void setNotifyTitle(String notifyTitle) {
            this.notifyTitle = notifyTitle;
        }

        public Set<String> getNotifyEventTypes() {
            return notifyEventTypes;
        }

        public void setNotifyEventTypes(Set<String> notifyEventTypes) {
            this.notifyEventTypes = notifyEventTypes;
        }

        public long getNotifyIntervalSec() {
            return notifyIntervalSec;
        }

        public void setNotifyIntervalSec(long notifyIntervalSec) {
            this.notifyIntervalSec = notifyIntervalSec;
        }

        public String[] getNotifyIncludeKeys() {
            return notifyIncludeKeys;
        }

        public void setNotifyIncludeKeys(String[] notifyIncludeKeys) {
            this.notifyIncludeKeys = notifyIncludeKeys;
        }

        public String[] getNotifyExcludeKeys() {
            return notifyExcludeKeys;
        }

        public void setNotifyExcludeKeys(String[] notifyExcludeKeys) {
            this.notifyExcludeKeys = notifyExcludeKeys;
        }
    }

    public TraceLog getTraceLog() {
        return traceLog;
    }

    public void setTraceLog(TraceLog traceLog) {
        this.traceLog = traceLog;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
