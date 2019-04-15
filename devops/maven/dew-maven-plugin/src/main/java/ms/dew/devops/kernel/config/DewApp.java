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

package ms.dew.devops.kernel.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Dew app.
 *
 * @author gudaoxuri
 */
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
    private Map<String, String> nodeSelector = new HashMap<String, String>() {
        {
            put("group", "app");
        }
    };
    private String buildCmd = "";
    private String runOptions = "-Xmx2688M -Xms2688M -Xmn960M -XX:MaxMetaspaceSize=512M "
            + "-XX:MetaspaceSize=512M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly "
            + "-XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses "
            + "-XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark -XX:+HeapDumpOnOutOfMemoryError";
    private Set<String> ignoreChangeFiles = new HashSet<>();

    /**
     * Gets replicas.
     *
     * @return the replicas
     */
    public int getReplicas() {
        return replicas;
    }

    /**
     * Sets replicas.
     *
     * @param replicas the replicas
     */
    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets metric port.
     *
     * @return the metric port
     */
    public int getMetricPort() {
        return
                metricPort;
    }

    /**
     * Sets metric port.
     *
     * @param metricPort the metric port
     */
    public void setMetricPort(int metricPort) {
        this.metricPort = metricPort;
    }

    /**
     * Gets liveness path.
     *
     * @return the liveness path
     */
    public String getLivenessPath() {
        return livenessPath;
    }

    /**
     * Sets liveness path.
     *
     * @param livenessPath the liveness path
     */
    public void setLivenessPath(String livenessPath) {
        this.livenessPath = livenessPath;
    }

    /**
     * Gets readiness path.
     *
     * @return the readiness path
     */
    public String getReadinessPath() {
        return readinessPath;
    }

    /**
     * Sets readiness path.
     *
     * @param readinessPath the readiness path
     */
    public void setReadinessPath(String readinessPath) {
        this.readinessPath = readinessPath;
    }

    /**
     * Gets revision history limit.
     *
     * @return the revision history limit
     */
    public int getRevisionHistoryLimit() {
        return revisionHistoryLimit;
    }

    /**
     * Sets revision history limit.
     *
     * @param revisionHistoryLimit the revision history limit
     */
    public void setRevisionHistoryLimit(int revisionHistoryLimit) {
        this.revisionHistoryLimit = revisionHistoryLimit;
    }

    /**
     * Gets liveness initial delay seconds.
     *
     * @return the liveness initial delay seconds
     */
    public int getLivenessInitialDelaySeconds() {
        return livenessInitialDelaySeconds;
    }

    /**
     * Sets liveness initial delay seconds.
     *
     * @param livenessInitialDelaySeconds the liveness initial delay seconds
     */
    public void setLivenessInitialDelaySeconds(int livenessInitialDelaySeconds) {
        this.livenessInitialDelaySeconds = livenessInitialDelaySeconds;
    }

    /**
     * Gets liveness period seconds.
     *
     * @return the liveness period seconds
     */
    public int getLivenessPeriodSeconds() {
        return livenessPeriodSeconds;
    }

    /**
     * Sets liveness period seconds.
     *
     * @param livenessPeriodSeconds the liveness period seconds
     */
    public void setLivenessPeriodSeconds(int livenessPeriodSeconds) {
        this.livenessPeriodSeconds = livenessPeriodSeconds;
    }

    /**
     * Gets readiness initial delay seconds.
     *
     * @return the readiness initial delay seconds
     */
    public int getReadinessInitialDelaySeconds() {
        return readinessInitialDelaySeconds;
    }

    /**
     * Sets readiness initial delay seconds.
     *
     * @param readinessInitialDelaySeconds the readiness initial delay seconds
     */
    public void setReadinessInitialDelaySeconds(int readinessInitialDelaySeconds) {
        this.readinessInitialDelaySeconds = readinessInitialDelaySeconds;
    }

    /**
     * Gets readiness period seconds.
     *
     * @return the readiness period seconds
     */
    public int getReadinessPeriodSeconds() {
        return readinessPeriodSeconds;
    }

    /**
     * Sets readiness period seconds.
     *
     * @param readinessPeriodSeconds the readiness period seconds
     */
    public void setReadinessPeriodSeconds(int readinessPeriodSeconds) {
        this.readinessPeriodSeconds = readinessPeriodSeconds;
    }

    /**
     * Gets liveness failure threshold.
     *
     * @return the liveness failure threshold
     */
    public int getLivenessFailureThreshold() {
        return livenessFailureThreshold;
    }

    /**
     * Sets liveness failure threshold.
     *
     * @param livenessFailureThreshold the liveness failure threshold
     */
    public void setLivenessFailureThreshold(int livenessFailureThreshold) {
        this.livenessFailureThreshold = livenessFailureThreshold;
    }

    /**
     * Gets readiness failure threshold.
     *
     * @return the readiness failure threshold
     */
    public int getReadinessFailureThreshold() {
        return readinessFailureThreshold;
    }

    /**
     * Sets readiness failure threshold.
     *
     * @param readinessFailureThreshold the readiness failure threshold
     */
    public void setReadinessFailureThreshold(int readinessFailureThreshold) {
        this.readinessFailureThreshold = readinessFailureThreshold;
    }

    /**
     * Is trace log enabled boolean.
     *
     * @return the boolean
     */
    public boolean isTraceLogEnabled() {
        return traceLogEnabled;
    }

    /**
     * Sets trace log enabled.
     *
     * @param traceLogEnabled the trace log enabled
     */
    public void setTraceLogEnabled(boolean traceLogEnabled) {
        this.traceLogEnabled = traceLogEnabled;
    }


    /**
     * Gets node selector.
     *
     * @return the node selector
     */
    public Map<String, String> getNodeSelector() {
        return nodeSelector;
    }

    /**
     * Sets node selector.
     *
     * @param nodeSelector the node selector
     */
    public void setNodeSelector(Map<String, String> nodeSelector) {
        this.nodeSelector = nodeSelector;
    }

    /**
     * Gets build cmd.
     *
     * @return the build cmd
     */
    public String getBuildCmd() {
        return buildCmd;
    }

    /**
     * Sets build cmd.
     *
     * @param buildCmd the build cmd
     */
    public void setBuildCmd(String buildCmd) {
        this.buildCmd = buildCmd;
    }

    /**
     * Gets run options.
     *
     * @return the run options
     */
    public String getRunOptions() {
        return runOptions;
    }

    /**
     * Sets run options.
     *
     * @param runOptions the run options
     */
    public void setRunOptions(String runOptions) {
        this.runOptions = runOptions;
    }

    /**
     * Gets ignore change files.
     *
     * @return the ignore change files
     */
    public Set<String> getIgnoreChangeFiles() {
        return ignoreChangeFiles;
    }

    /**
     * Sets ignore change files.
     *
     * @param ignoreChangeFiles the ignore change files
     */
    public void setIgnoreChangeFiles(Set<String> ignoreChangeFiles) {
        this.ignoreChangeFiles = ignoreChangeFiles;
    }
}
