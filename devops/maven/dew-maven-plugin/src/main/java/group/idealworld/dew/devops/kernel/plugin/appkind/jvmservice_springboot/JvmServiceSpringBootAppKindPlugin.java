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

package group.idealworld.dew.devops.kernel.plugin.appkind.jvmservice_springboot;

import com.ecfront.dew.common.$;
import com.fasterxml.jackson.databind.JsonNode;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.flow.debug.DefaultDebugFlow;
import group.idealworld.dew.devops.kernel.flow.log.DefaultLogFlow;
import group.idealworld.dew.devops.kernel.flow.scale.DefaultScaleFlow;
import group.idealworld.dew.devops.kernel.flow.unrelease.DefaultUnReleaseFlow;
import group.idealworld.dew.devops.kernel.helper.YamlHelper;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.refresh.DefaultRefreshFlow;
import group.idealworld.dew.devops.kernel.flow.release.KubeReleaseFlow;
import group.idealworld.dew.devops.kernel.flow.rollback.DefaultRollbackFlow;
import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Spring boot app kind plugin.
 *
 * @author gudaoxuri
 */
public class JvmServiceSpringBootAppKindPlugin implements AppKindPlugin {

    @Override
    public String getName() {
        return "Spring Boot Backend";
    }

    @Override
    public void customConfig(FinalProjectConfig projectConfig) {
        Arrays.stream(new File(projectConfig.getDirectory()
                + "src" + File.separator
                + "main" + File.separator
                + "resources").listFiles())
                .filter((res -> res.getName().toLowerCase().contains("application")
                        || res.getName().toLowerCase().contains("bootstrap")))
                .map(file -> {
                    try {
                        if (file.getName().toLowerCase().endsWith("yaml") || file.getName().toLowerCase().endsWith("yml")) {
                            Map config = YamlHelper.toObject($.file.readAllByFile(file, "UTF-8"));
                            if (config.containsKey("spring")
                                    && ((Map) config.get("spring")).containsKey("application")
                                    && ((Map) ((Map) config.get("spring")).get("application")).containsKey("name")) {
                                return ((Map) ((Map) config.get("spring")).get("application")).get("name").toString();
                            }
                        } else if (file.getName().toLowerCase().endsWith("properties")) {
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(file));
                            if (properties.containsKey("spring.application.name")) {
                                return properties.getProperty("spring.application.name");
                            }
                        } else if (file.getName().toLowerCase().endsWith("json")) {
                            JsonNode config = $.json.toJson($.file.readAllByFile(file, "UTF-8"));
                            if (config.has("spring")
                                    && config.get("spring").has("application")
                                    && config.get("spring").get("application").has("name")) {
                                return config.get("spring").get("application").get("name").asText();
                            }
                        }
                        return null;
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(projectConfig::setAppName);
        if (projectConfig.getApp().getPort() == null) {
            projectConfig.getApp().setPort(8080);
        }
        if (projectConfig.getApp().getHealthCheckEnabled() == null) {
            projectConfig.getApp().setHealthCheckEnabled(true);
        }
        if (projectConfig.getApp().getHealthCheckEnabled() && projectConfig.getApp().getHealthCheckPort() == null) {
            projectConfig.getApp().setHealthCheckPort(projectConfig.getApp().getPort());
        }
        if (projectConfig.getApp().getTraceLogEnabled() == null) {
            projectConfig.getApp().setTraceLogEnabled(true);
        }
        if (projectConfig.getApp().getMetricsEnabled() == null) {
            projectConfig.getApp().setMetricsEnabled(true);
        }
        if (projectConfig.getApp().getTraceLogSpans() == null) {
            projectConfig.getApp().setTraceLogSpans(false);
        }
        if (projectConfig.getApp().getTraceProbabilisticSamplingRate() == null) {
            projectConfig.getApp().setTraceProbabilisticSamplingRate(0.1);
        }
        if (projectConfig.getApp().getLivenessPath() == null) {
            projectConfig.getApp().setLivenessPath("/actuator/health");
        }
        if (projectConfig.getApp().getReadinessPath() == null) {
            projectConfig.getApp().setReadinessPath("/actuator/health");
        }
        if (projectConfig.getApp().getTraceWebSkipPattern() == null) {
            projectConfig.getApp().setTraceWebSkipPattern(
                    "/api-docs.*|/swagger.*|.*\\\\.png|.*\\\\.css|.*\\\\.js|.*\\\\.html|/favicon.ico|/hystrix.stream"
                            + "|/actuator.*");
        }
    }

    @Override
    public BasicFlow prepareFlow() {
        return new JvmServiceSpringBootPrepareFlow();
    }

    @Override
    public BasicFlow buildFlow() {
        return new JvmServiceSpringBootBuildFlow();
    }

    @Override
    public BasicFlow releaseFlow() {
        return new KubeReleaseFlow();
    }

    @Override
    public BasicFlow unReleaseFlow() {
        return new DefaultUnReleaseFlow();
    }

    @Override
    public BasicFlow rollbackFlow(boolean history, String version) {
        return new DefaultRollbackFlow(history, version);
    }

    @Override
    public BasicFlow scaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg) {
        return new DefaultScaleFlow(replicas, autoScale, minReplicas, maxReplicas, cpuAvg);
    }

    @Override
    public BasicFlow refreshFlow() {
        return new DefaultRefreshFlow();
    }

    @Override
    public BasicFlow logFlow(String podName, boolean follow) {
        return new DefaultLogFlow(podName, follow);
    }

    @Override
    public BasicFlow debugFlow(String podName, int forwardPort) {
        return new DefaultDebugFlow(podName, forwardPort);
    }

    @Override
    public Map<String, String> getEnv(FinalProjectConfig projectConfig) {
        return new HashMap<String, String>() {
            {
                put("JAVA_OPTIONS", setJavaOptionsValue(projectConfig));
                put("JAVA_DEBUG_OPTIONS", " -Dspring.profiles.active=" + projectConfig.getProfile() + " -Dserver.port="
                        + projectConfig.getApp().getDebugPort());
            }
        };
    }

    private String setJavaOptionsValue(FinalProjectConfig config) {
        String containerEnvJavaOptionsValue =
                (config.getApp().getRunOptions() == null ? "" : config.getApp().getRunOptions())
                        + " -Dspring.profiles.active=" + config.getProfile()
                        + " -Dserver.port=" + config.getApp().getPort();
        containerEnvJavaOptionsValue += " -Dopentracing.jaeger.log-spans=" + config.getApp().getTraceLogSpans();
        if (config.getApp().getTraceLogEnabled()) {
            if (!config.getApp().getTraceProbabilisticSamplingRate().equals(1.0)) {
                containerEnvJavaOptionsValue += " -Dopentracing.jaeger.probabilistic-sampler.sampling-rate="
                        + config.getApp().getTraceProbabilisticSamplingRate();
            }
            if (!config.getApp().getTraceWebSkipPattern().isEmpty()) {
                containerEnvJavaOptionsValue += " -Dopentracing.spring.web.skip-pattern=" + config.getApp().getTraceWebSkipPattern();
            }
        }
        if (config.getApp().getMetricsEnabled()) {
            containerEnvJavaOptionsValue += " -Dmanagement.endpoints.web.exposure.include=*"
                    + " -Dmanagement.metrics.tags.application=" + config.getAppName();
        }
        return containerEnvJavaOptionsValue;
    }

}
