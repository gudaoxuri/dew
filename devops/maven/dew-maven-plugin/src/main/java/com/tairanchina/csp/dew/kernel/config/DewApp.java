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

package com.tairanchina.csp.dew.kernel.config;

import java.util.HashSet;
import java.util.Set;

public class DewApp {

    private int replicas = 1;
    private int revisionHistoryLimit = 3;
    private int port = 8080;
    private int metricPort = 9779;
    private String livenessPath = "/actuator/health";
    private String readinessPath = "/actuator/health";
    private int livenessInitialDelaySeconds = 60;
    private int livenessPeriodSeconds = 30;
    private int livenessFailureThreshold = 6;
    private int readinessInitialDelaySeconds = 10;
    private int readinessPeriodSeconds = 60;
    private int readinessFailureThreshold = 3;
    private boolean traceLogEnabled = true;
    private String javaOptions = "-Xmx2688M -Xms2688M -Xmn960M -XX:MaxMetaspaceSize=512M -XX:MetaspaceSize=512M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:+HeapDumpOnOutOfMemoryError";
    private Set<String> ignoreChangeFiles = new HashSet<>();

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMetricPort() {
        return
                metricPort;
    }

    public void setMetricPort(int metricPort) {
        this.metricPort = metricPort;
    }

    public String getLivenessPath() {
        return livenessPath;
    }

    public void setLivenessPath(String livenessPath) {
        this.livenessPath = livenessPath;
    }

    public String getReadinessPath() {
        return readinessPath;
    }

    public void setReadinessPath(String readinessPath) {
        this.readinessPath = readinessPath;
    }

    public int getRevisionHistoryLimit() {
        return revisionHistoryLimit;
    }

    public void setRevisionHistoryLimit(int revisionHistoryLimit) {
        this.revisionHistoryLimit = revisionHistoryLimit;
    }

    public int getLivenessInitialDelaySeconds() {
        return livenessInitialDelaySeconds;
    }

    public void setLivenessInitialDelaySeconds(int livenessInitialDelaySeconds) {
        this.livenessInitialDelaySeconds = livenessInitialDelaySeconds;
    }

    public int getLivenessPeriodSeconds() {
        return livenessPeriodSeconds;
    }

    public void setLivenessPeriodSeconds(int livenessPeriodSeconds) {
        this.livenessPeriodSeconds = livenessPeriodSeconds;
    }

    public int getReadinessInitialDelaySeconds() {
        return readinessInitialDelaySeconds;
    }

    public void setReadinessInitialDelaySeconds(int readinessInitialDelaySeconds) {
        this.readinessInitialDelaySeconds = readinessInitialDelaySeconds;
    }

    public int getReadinessPeriodSeconds() {
        return readinessPeriodSeconds;
    }

    public void setReadinessPeriodSeconds(int readinessPeriodSeconds) {
        this.readinessPeriodSeconds = readinessPeriodSeconds;
    }

    public int getLivenessFailureThreshold() {
        return livenessFailureThreshold;
    }

    public void setLivenessFailureThreshold(int livenessFailureThreshold) {
        this.livenessFailureThreshold = livenessFailureThreshold;
    }

    public int getReadinessFailureThreshold() {
        return readinessFailureThreshold;
    }

    public void setReadinessFailureThreshold(int readinessFailureThreshold) {
        this.readinessFailureThreshold = readinessFailureThreshold;
    }

    public boolean isTraceLogEnabled() {
        return traceLogEnabled;
    }

    public void setTraceLogEnabled(boolean traceLogEnabled) {
        this.traceLogEnabled = traceLogEnabled;
    }

    public String getJavaOptions() {
        return javaOptions;
    }

    public void setJavaOptions(String javaOptions) {
        this.javaOptions = javaOptions;
    }

    public Set<String> getIgnoreChangeFiles() {
        return ignoreChangeFiles;
    }

    public void setIgnoreChangeFiles(Set<String> ignoreChangeFiles) {
        this.ignoreChangeFiles = ignoreChangeFiles;
    }
}
