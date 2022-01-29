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

package group.idealworld.dew.devops.kernel.config;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1ContainerPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dew app.
 *
 * @author gudaoxuri
 */
public class DewApp {

    // 部署的副本数
    private Integer replicas = 1;
    // 保留的历史版本数
    private Integer revisionHistoryLimit = 3;
    // 端口号，默认情况下前端项目为80(不可修改)，后端服务为8080
    private Integer port;
    // 扩展端口，仅用于后端服务
    private List<V1ContainerPort> extendedPorts;
    // 远程调试服务端口号，仅用于后端，默认是9000
    private Integer debugPort = 9000;
    // 是否启用健康监控，仅用于后端服务
    private Boolean healthCheckEnabled;
    // 健康检查使用端口，仅用于后端服务
    private Integer healthCheckPort;
    // 存活检测HTTP的路径，仅用于后端服务
    private String livenessPath;
    // 可用检测HTTP的路径，仅用于后端服务
    private String readinessPath;
    // 首次存活检测延迟时间，仅用于后端服务
    private Integer livenessInitialDelaySeconds = 30;
    // 存活检测周期，仅用于后端服务
    private Integer livenessPeriodSeconds = 30;
    // 存活检测失败次数阈值，超过后销毁当前实例并重启另一个实例，仅用于后端服务
    private Integer livenessFailureThreshold = 6;
    // 首次可用检测延迟时间，仅用于后端服务
    private Integer readinessInitialDelaySeconds = 30;
    // 可用检测周期，仅用于后端服务
    private Integer readinessPeriodSeconds = 30;
    // 可用检测失败次数阈值，超过后当前实例不可用，仅用于后端服务
    private Integer readinessFailureThreshold = 3;
    // 是否启用追踪日志，仅用于后端服务
    private Boolean traceLogEnabled;
    // 是否在控制台输出spans日志，仅用于后端服务
    private Boolean traceLogSpans;
    // 设置跳过追踪的接口，为空则使用官方默认值，仅用于后端服务
    // @see https://github.com/opentracing-contrib/java-spring-web/blob/master/opentracing-spring-web-starter/src/main/java/io/opentracing/contrib/spring/web/starter/WebTracingProperties.java
    private String traceWebSkipPattern;
    // 追踪日志概率采样比率，为1.0则使用全量采样，仅用于后端服务
    private Double traceProbabilisticSamplingRate;
    // 是否启用Prometheus的metrics，仅用于后端服务
    private Boolean metricsEnabled;
    // 节点亲和性配置
    // 默认选择标签为 group=app 的节点
    private Map<String, String> nodeSelector = new HashMap<>();
    // volumeAmounts配置 不填写名称默认为namespace
    private List<Map<String, String>> volumeMounts = new ArrayList<>();
    // volume配置
    private List<Map<String, String>> volumes = new ArrayList<>();
    // 预打包命令
    // 前端项目默认为 cd <项目目录> && set NODE_ENV=<环境名称> && npm install，
    // 发现不存在 node_modules 或 发现前端项目下 package.json 文件变更 时执行
    // 后端服务默认为空
    private String preparePackageCmd;
    // 打包命令
    // 前端项目默认为 cd <项目目录> && set NODE_ENV=<环境名称> npm run build:<环境名称>
    // 后端服务默认为空
    private String packageCmd;
    // 服务配置，多为Nginx配置
    private String serverConfig;
    // 运行参数，可指定诸如 JVM 配置等信息
    private String runOptions;
    // 容器command命令
    private List<String> containerCmd;
    // 容器资源上限，同Kubernetes配置
    // @see  https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/
    private Map<String, Quantity> containerResourcesLimits = new HashMap<>();
    // 容器资源下限，同Kubernetes配置
    // @see  https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/
    private Map<String, Quantity> containerResourcesRequests = new HashMap<>();
    // annotations配置
    private Map<String, String> annotations = new HashMap<>();
    // labels配置
    private Map<String, String> labels = new HashMap<>();
    // env配置
    private Map<String, String> env = new HashMap<>();

    /**
     * Gets replicas.
     *
     * @return the replicas
     */
    public Integer getReplicas() {
        return replicas;
    }

    /**
     * Sets replicas.
     *
     * @param replicas the replicas
     */
    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    /**
     * Gets revision history limit.
     *
     * @return the revision history limit
     */
    public Integer getRevisionHistoryLimit() {
        return revisionHistoryLimit;
    }

    /**
     * Sets revision history limit.
     *
     * @param revisionHistoryLimit the revision history limit
     */
    public void setRevisionHistoryLimit(Integer revisionHistoryLimit) {
        this.revisionHistoryLimit = revisionHistoryLimit;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets extended ports.
     *
     * @return the extendedPorts
     */
    public List<V1ContainerPort> getExtendedPorts() {
        return extendedPorts;
    }

    /**
     * Sets extended ports.
     *
     * @param extendedPorts the extendedPorts
     */
    public void setExtendedPorts(List<V1ContainerPort> extendedPorts) {
        this.extendedPorts = extendedPorts;
    }

    /**
     * Gets debug port.
     *
     * @return the debugPort
     */
    public Integer getDebugPort() {
        return debugPort;
    }

    /**
     * Sets extended ports.
     *
     * @param debugPort the debugPort
     */
    public void setDebugPort(Integer debugPort) {
        this.debugPort = debugPort;
    }

    /**
     * Gets health check port.
     *
     * @return the healthCheckPort
     */
    public Integer getHealthCheckPort() {
        return healthCheckPort;
    }

    /**
     * Sets health check port.
     *
     * @param healthCheckPort the healthCheckPort
     */
    public void setHealthCheckPort(Integer healthCheckPort) {
        this.healthCheckPort = healthCheckPort;
    }

    /**
     * Gets health check enabled.
     *
     * @return the healthCheckEnabled
     */
    public Boolean getHealthCheckEnabled() {
        return healthCheckEnabled;
    }

    /**
     * Sets health check enabled.
     *
     * @param healthCheckEnabled the healthCheckEnabled
     */
    public void setHealthCheckEnabled(Boolean healthCheckEnabled) {
        this.healthCheckEnabled = healthCheckEnabled;
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
     * Gets liveness initial delay seconds.
     *
     * @return the liveness initial delay seconds
     */
    public Integer getLivenessInitialDelaySeconds() {
        return livenessInitialDelaySeconds;
    }

    /**
     * Sets liveness initial delay seconds.
     *
     * @param livenessInitialDelaySeconds the liveness initial delay seconds
     */
    public void setLivenessInitialDelaySeconds(Integer livenessInitialDelaySeconds) {
        this.livenessInitialDelaySeconds = livenessInitialDelaySeconds;
    }

    /**
     * Gets liveness period seconds.
     *
     * @return the liveness period seconds
     */
    public Integer getLivenessPeriodSeconds() {
        return livenessPeriodSeconds;
    }

    /**
     * Sets liveness period seconds.
     *
     * @param livenessPeriodSeconds the liveness period seconds
     */
    public void setLivenessPeriodSeconds(Integer livenessPeriodSeconds) {
        this.livenessPeriodSeconds = livenessPeriodSeconds;
    }

    /**
     * Gets liveness failure threshold.
     *
     * @return the liveness failure threshold
     */
    public Integer getLivenessFailureThreshold() {
        return livenessFailureThreshold;
    }

    /**
     * Sets liveness failure threshold.
     *
     * @param livenessFailureThreshold the liveness failure threshold
     */
    public void setLivenessFailureThreshold(Integer livenessFailureThreshold) {
        this.livenessFailureThreshold = livenessFailureThreshold;
    }

    /**
     * Gets readiness initial delay seconds.
     *
     * @return the readiness initial delay seconds
     */
    public Integer getReadinessInitialDelaySeconds() {
        return readinessInitialDelaySeconds;
    }

    /**
     * Sets readiness initial delay seconds.
     *
     * @param readinessInitialDelaySeconds the readiness initial delay seconds
     */
    public void setReadinessInitialDelaySeconds(Integer readinessInitialDelaySeconds) {
        this.readinessInitialDelaySeconds = readinessInitialDelaySeconds;
    }

    /**
     * Gets readiness period seconds.
     *
     * @return the readiness period seconds
     */
    public Integer getReadinessPeriodSeconds() {
        return readinessPeriodSeconds;
    }

    /**
     * Sets readiness period seconds.
     *
     * @param readinessPeriodSeconds the readiness period seconds
     */
    public void setReadinessPeriodSeconds(Integer readinessPeriodSeconds) {
        this.readinessPeriodSeconds = readinessPeriodSeconds;
    }

    /**
     * Gets readiness failure threshold.
     *
     * @return the readiness failure threshold
     */
    public Integer getReadinessFailureThreshold() {
        return readinessFailureThreshold;
    }

    /**
     * Sets readiness failure threshold.
     *
     * @param readinessFailureThreshold the readiness failure threshold
     */
    public void setReadinessFailureThreshold(Integer readinessFailureThreshold) {
        this.readinessFailureThreshold = readinessFailureThreshold;
    }

    /**
     * Gets trace log enabled.
     *
     * @return the trace log enabled
     */
    public Boolean getTraceLogEnabled() {
        return traceLogEnabled;
    }

    /**
     * Sets trace log enabled.
     *
     * @param traceLogEnabled the trace log enabled
     */
    public void setTraceLogEnabled(Boolean traceLogEnabled) {
        this.traceLogEnabled = traceLogEnabled;
    }

    /**
     * Gets trace log spans.
     *
     * @return the trace log spans
     */
    public Boolean getTraceLogSpans() {
        return traceLogSpans;
    }

    /**
     * Sets trace log spans.
     *
     * @param traceLogSpans the trace log spans
     */
    public void setTraceLogSpans(Boolean traceLogSpans) {
        this.traceLogSpans = traceLogSpans;
    }

    /**
     * Gets trace web skip pattern.
     *
     * @return the trace web skip pattern
     */
    public String getTraceWebSkipPattern() {
        return traceWebSkipPattern;
    }

    /**
     * Sets trace web skip pattern.
     *
     * @param traceWebSkipPattern the trace web skip pattern
     */
    public void setTraceWebSkipPattern(String traceWebSkipPattern) {
        this.traceWebSkipPattern = traceWebSkipPattern;
    }

    /**
     * Gets trace probabilistic sampling rate.
     *
     * @return the trace probabilistic sampling rate
     */
    public Double getTraceProbabilisticSamplingRate() {
        return traceProbabilisticSamplingRate;
    }

    /**
     * Sets trace probabilistic sampling rate.
     *
     * @param traceProbabilisticSamplingRate the trace probabilistic sampling rate
     */
    public void setTraceProbabilisticSamplingRate(Double traceProbabilisticSamplingRate) {
        this.traceProbabilisticSamplingRate = traceProbabilisticSamplingRate;
    }

    /**
     * Gets metrics enabled.
     *
     * @return the metrics enabled
     */
    public Boolean getMetricsEnabled() {
        return metricsEnabled;
    }

    /**
     * Sets metrics enabled.
     *
     * @param metricsEnabled the metrics enabled
     */
    public void setMetricsEnabled(Boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
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
     * Gets volume mounts.
     *
     * @return the volume mounts
     */
    public List<Map<String, String>> getVolumeMounts() {
        return volumeMounts;
    }

    /**
     * Sets volume mounts.
     *
     * @param volumeMounts the volume mounts
     */
    public void setVolumeMounts(List<Map<String, String>> volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    /**
     * Gets volumes.
     *
     * @return the volumes
     */
    public List<Map<String, String>> getVolumes() {
        return volumes;
    }

    /**
     * Sets volumes.
     *
     * @param volumes the volumes
     */
    public void setVolumes(List<Map<String, String>> volumes) {
        this.volumes = volumes;
    }

    /**
     * Gets prepare package cmd.
     *
     * @return the prepare package cmd
     */
    public String getPreparePackageCmd() {
        return preparePackageCmd;
    }

    /**
     * Sets prepare package cmd.
     *
     * @param preparePackageCmd the prepare package cmd
     */
    public void setPreparePackageCmd(String preparePackageCmd) {
        this.preparePackageCmd = preparePackageCmd;
    }

    /**
     * Gets package cmd.
     *
     * @return the package cmd
     */
    public String getPackageCmd() {
        return packageCmd;
    }

    /**
     * Sets package cmd.
     *
     * @param packageCmd the package cmd
     */
    public void setPackageCmd(String packageCmd) {
        this.packageCmd = packageCmd;
    }

    /**
     * Gets server config.
     *
     * @return the server config
     */
    public String getServerConfig() {
        return serverConfig;
    }

    /**
     * Sets server config.
     *
     * @param serverConfig the server config
     */
    public void setServerConfig(String serverConfig) {
        this.serverConfig = serverConfig;
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
     * Gets container command.
     *
     * @return the container command
     */
    public List<String> getContainerCmd() {
        return containerCmd;
    }

    /**
     * Sets container command.
     *
     * @param containerCmd the container command
     */
    public void setContainerCmd(List<String> containerCmd) {
        this.containerCmd = containerCmd;
    }

    /**
     * Gets container resources limits.
     *
     * @return the container resources limits
     */
    public Map<String, Quantity> getContainerResourcesLimits() {
        return containerResourcesLimits;
    }

    /**
     * Sets container resources limits.
     *
     * @param containerResourcesLimits the container resources limits
     */
    public void setContainerResourcesLimits(Map<String, Quantity> containerResourcesLimits) {
        this.containerResourcesLimits = containerResourcesLimits;
    }

    /**
     * Gets container resources requests.
     *
     * @return the container resources requests
     */
    public Map<String, Quantity> getContainerResourcesRequests() {
        return containerResourcesRequests;
    }

    /**
     * Sets container resources requests.
     *
     * @param containerResourcesRequests the container resources requests
     */
    public void setContainerResourcesRequests(Map<String, Quantity> containerResourcesRequests) {
        this.containerResourcesRequests = containerResourcesRequests;
    }

    /**
     * Gets annotations.
     *
     * @return the annotations
     */
    public Map<String, String> getAnnotations() {
        return annotations;
    }

    /**
     * Sets annotations.
     *
     * @param annotations the annotations
     */
    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    /**
     * Gets labels.
     *
     * @return the labels
     */
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Sets labels.
     *
     * @param labels the labels
     */
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * Gets env.
     *
     * @return the env
     */
    public Map<String, String> getEnv() {
        return env;
    }

    /**
     * Sets env.
     *
     * @param env the env
     */
    public void setEnv(Map<String, String> env) {
        this.env = env;
    }
}
