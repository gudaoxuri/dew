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

package group.idealworld.dew.devops.kernel.function;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.notification.Notify;
import group.idealworld.dew.core.notification.NotifyConfig;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Execute event processor.
 *
 * @author gudaoxuri
 */
public class ExecuteEventProcessor {

    private static boolean hasGlobalError = false;

    /**
     * Init.
     *
     * @param processingProjects the processing projects
     */
    public static void init(List<FinalProjectConfig> processingProjects) {
        if (processingProjects.isEmpty()) {
            SendFactory.initWithEmpty();
            return;
        }
        SendFactory.init(processingProjects.get(0).getProfile(),
                processingProjects.stream()
                        .map(FinalProjectConfig::getAppShowName)
                        .collect(Collectors.toList()));
    }

    /**
     * On mojo execute successful.
     *
     * @param mojoName      the mojo name
     * @param projectConfig the project config
     * @param message       the message
     */
    public static void onMojoExecuteSuccessful(String mojoName, FinalProjectConfig projectConfig, String message) {
        projectConfig.getExecuteSuccessfulMojos().add(mojoName);
        if (!mojoName.equalsIgnoreCase("release")
                && !mojoName.equalsIgnoreCase("rollback")
                && !mojoName.equalsIgnoreCase("scale")
                && !mojoName.equalsIgnoreCase("unrelease")
                && !mojoName.equalsIgnoreCase("refresh")
        ) {
            return;
        }
        SendFactory.onMojoExecute(mojoName, projectConfig, message, null);
    }

    /**
     * On mojo execute failure.
     *
     * @param mojoName      the mojo name
     * @param projectConfig the project config
     * @param throwable     the throwable
     */
    public static void onMojoExecuteFailure(String mojoName, FinalProjectConfig projectConfig, Throwable throwable) {
        SendFactory.onMojoExecute(mojoName, projectConfig, "", throwable);
    }

    /**
     * On global process error.
     *
     * @param throwable the throwable
     */
    public static void onGlobalProcessError(Throwable throwable) {
        hasGlobalError = true;
        SendFactory.onGlobalProcessError(throwable);
    }

    /**
     * On shutdown.
     *
     * @param projects the projects
     */
    public static void onShutdown(Map<String, FinalProjectConfig> projects) {
        if (hasGlobalError) {
            return;
        }
        SendFactory.onShutdown(projects);
    }

    private static class SendFactory {

        private static SendToDD sendToDD = new SendToDD();
        private static SendToHttp sendToHttp = new SendToHttp();

        static void initWithEmpty() {
            sendToDD.initWithEmpty();
            sendToHttp.initWithEmpty();
        }

        static void init(String profile, List<String> appShowNames) {
            sendToDD.init(profile, appShowNames);
            sendToHttp.init(profile, appShowNames);
        }

        static void onMojoExecute(String mojoName, FinalProjectConfig projectConfig, String message, Throwable throwable) {
            sendToDD.onMojoExecute(mojoName, projectConfig.getId(), projectConfig.getProfile(), projectConfig.getAppShowName(),
                    projectConfig.getAppGroup(), message, throwable);
            sendToHttp.onMojoExecute(mojoName, projectConfig.getId(), projectConfig.getProfile(), projectConfig.getAppShowName(),
                    projectConfig.getAppGroup(), message, throwable);
        }

        static void onGlobalProcessError(Throwable throwable) {
            sendToDD.onGlobalProcessError(throwable);
            sendToHttp.onGlobalProcessError(throwable);
        }

        static void onShutdown(Map<String, FinalProjectConfig> projects) {
            List<FinalProjectConfig> executionSuccessfulProjects = projects.values().stream()
                    .filter(project -> project.getExecuteSuccessfulMojos().contains("release")
                            || project.getExecuteSuccessfulMojos().contains("rollback")
                            || project.getExecuteSuccessfulMojos().contains("scale")
                            || project.getExecuteSuccessfulMojos().contains("unrelease")
                            || project.getExecuteSuccessfulMojos().contains("refresh")
                    ).collect(Collectors.toList());
            List<FinalProjectConfig> nonExecutionProjects =
                    projects.values().stream()
                            .filter(project -> !executionSuccessfulProjects.contains(project))
                            .collect(Collectors.toList());
            sendToDD.onShutdown(projects, executionSuccessfulProjects, nonExecutionProjects);
            sendToHttp.onShutdown(projects, executionSuccessfulProjects, nonExecutionProjects);
        }
    }

    private interface Send {

        String appendCIJobUrl();

        boolean skipNotify();

        default void doSend(String flag, String type, String content, String title) {
            Notify.send(flag + "_" + type, content, title);
        }

        void initWithEmpty();

        void init(String profile, List<String> appShowNames);

        default String getCIJobUrl() {
            String ciJobUrl = null;
            if (System.getProperties().containsKey("CI_JOB_URL")) {
                ciJobUrl = System.getProperty("CI_JOB_URL");
            }
            if (System.getenv().containsKey("CI_JOB_URL")) {
                ciJobUrl = System.getenv("CI_JOB_URL");
            }
            // 环境变量 BUILD_URL 适用于 Jenkins Job 构建时
            if (System.getenv().containsKey("BUILD_URL")) {
                ciJobUrl = System.getenv("BUILD_URL");
            }
            if (ciJobUrl != null) {
                return ciJobUrl;
            }
            return "";
        }

        void onMojoExecute(String mojoName, String flag, String profile, String appShowName, String appGroup, String message, Throwable throwable);

        void onGlobalProcessError(Throwable throwable);

        void onShutdown(Map<String, FinalProjectConfig> projects,
                        List<FinalProjectConfig> executionSuccessfulProjects,
                        List<FinalProjectConfig> nonExecutionProjects);

    }

    private static class SendToDD implements Send {

        @Override
        public String appendCIJobUrl() {
            return "\n----------\n> See [CI Job](" + getCIJobUrl() + ")\n";
        }

        @Override
        public boolean skipNotify() {
            return !Notify.contains("_" + NotifyConfig.TYPE_DD);
        }

        @Override
        public void initWithEmpty() {
            if (skipNotify()) {
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.idealworld.group/images/devops-notify/report.png)")
                    .append("\n")
                    .append("No projects need to be processed.")
                    .append("\n\n")
                    .append(appendCIJobUrl());
            doSend("", NotifyConfig.TYPE_DD, content.toString(), "DevOps process report");
        }

        @Override
        public void init(String profile, List<String> appShowNames) {
            if (skipNotify()) {
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.idealworld.group/images/devops-notify/report.png)")
                    .append("\n")
                    .append("### Processing Projects @ [" + profile + "]\n")
                    .append("\n\n-----------------\n");
            content.append(appShowNames.stream()
                    .map(name -> "- " + name)
                    .collect(Collectors.joining("\n")))
                    .append("\n\n")
                    .append(appendCIJobUrl());
            doSend("", NotifyConfig.TYPE_DD, content.toString(), "DevOps process report");
        }

        @Override
        public void onMojoExecute(String mojoName, String flag, String profile,
                                  String appShowName, String appGroup,
                                  String message, Throwable throwable) {
            if (skipNotify()) {
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.idealworld.group/images/devops-notify/" + (throwable != null ? "failure" : "successful") + ".png)")
                    .append("\n")
                    .append("# " + appShowName + "\n")
                    .append("> " + appGroup + "\n")
                    .append("\n")
                    .append("## " + (throwable != null ? "Failure" : "Successful"))
                    .append(" : [" + mojoName + "] @ [" + profile + "]\n");
            if (message != null && !message.trim().isEmpty()
                    || throwable != null) {
                content.append("> ---------\n")
                        .append("> " + message + "\n")
                        .append("> " + throwable + "\n");
            }
            content.append("\n\n")
                    .append(appendCIJobUrl());
            if (throwable != null) {
                doSend(flag, NotifyConfig.TYPE_DD, content.toString(), "DevOps process failure");
            } else {
                doSend(flag, NotifyConfig.TYPE_DD, content.toString(), "DevOps process successful");
            }
        }

        @Override
        public void onGlobalProcessError(Throwable throwable) {
            if (skipNotify()) {
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.idealworld.group/images/devops-notify/report.png)")
                    .append("\n")
                    .append("Execution error" + (throwable.getMessage() != null ? ": " + throwable.getMessage() : ""))
                    .append("\n")
                    .append(Arrays
                            .stream(throwable.getStackTrace())
                            .map(e -> "> " + e.toString())
                            .limit(20)
                            .collect(Collectors.joining("\n")))
                    .append("\n\n")
                    .append(appendCIJobUrl());
            doSend("", NotifyConfig.TYPE_DD, content.toString(), "DevOps process report");
        }

        @Override
        public void onShutdown(Map<String, FinalProjectConfig> projects,
                               List<FinalProjectConfig> executionSuccessfulProjects,
                               List<FinalProjectConfig> nonExecutionProjects) {
            if (skipNotify()) {
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.idealworld.group/images/devops-notify/report.png)")
                    .append("\n")
                    .append("## Execute Successful\n")
                    .append("![](http://doc.dew.idealworld.group/images/devops-notify/successful-split.png)")
                    .append("\n");
            content.append(executionSuccessfulProjects.stream()
                    .map(project -> "- " + project.getAppShowName())
                    .collect(Collectors.joining("\n")));

            content.append("\n\n")
                    .append("## Execute Failure\n")
                    .append("![](http://doc.dew.idealworld.group/images/devops-notify/failure-split.png)")
                    .append("\n");
            content.append(nonExecutionProjects.stream()
                    .filter(FinalProjectConfig::isHasError)
                    .map(project -> {
                        String reason = project.getSkipReason().isEmpty() ? "unknown error" : project.getSkipReason();
                        return "- " + project.getAppShowName() + "\n> " + reason;
                    })
                    .collect(Collectors.joining("\n")));
            content.append("\n\n")
                    .append("## Non-execution\n")
                    .append("![](http://doc.dew.idealworld.group/images/devops-notify/non-split.png)")
                    .append("\n");
            content.append(nonExecutionProjects.stream()
                    .filter(project -> !project.isHasError() && !project.getSkip())
                    .map(project -> "- " + project.getAppShowName() + "\n> " + project.getSkipReason())
                    .collect(Collectors.joining("\n")));
            content.append("\n\n")
                    .append("## Ignore execution\n")
                    .append("![](http://doc.dew.idealworld.group/images/devops-notify/ignore-split.png)")
                    .append("\n");
            content.append(projects.values().stream()
                    .filter(project -> !project.isHasError() && project.getSkip())
                    .map(project -> "- " + project.getAppShowName() + "\n> " + project.getSkipReason())
                    .collect(Collectors.joining("\n")));
            content.append("\n\n")
                    .append(appendCIJobUrl());
            doSend("", NotifyConfig.TYPE_DD, content.toString(), "DevOps process report");
        }


    }

    private static class SendToHttp implements Send {

        private static final String PROCESS_EMPTY_FLAG = "PROCESS_EMPTY";
        private static final String PROCESS_START_FLAG = "PROCESS_START";
        private static final String PROCESS_EXECUTE_SUCCESS_FLAG = "PROCESS_EXECUTE_SUCCESS";
        private static final String PROCESS_EXECUTE_FAILURE_FLAG = "PROCESS_EXECUTE_FAILURE";
        private static final String PROCESS_GLOBAL_ERROR_FLAG = "PROCESS_GLOBAL_ERROR";
        private static final String PROCESS_SHUTDOWN_FLAG = "PROCESS_SHUTDOWN";

        @Override
        public String appendCIJobUrl() {
            return getCIJobUrl();
        }

        @Override
        public boolean skipNotify() {
            return !Notify.contains("_" + NotifyConfig.TYPE_HTTP);
        }

        @Override
        public void initWithEmpty() {
            if (skipNotify()) {
                return;
            }
            doSend("", NotifyConfig.TYPE_HTTP, build(PROCESS_EMPTY_FLAG, new HashMap<>()), "DevOps process report");
        }

        @Override
        public void init(String profile, List<String> appShowNames) {
            if (skipNotify()) {
                return;
            }
            Map<String, Object> message = new HashMap<>() {
                {
                    put("profile", profile);
                    put("projects", appShowNames);
                }
            };
            doSend("", NotifyConfig.TYPE_HTTP, build(PROCESS_START_FLAG, message), "DevOps process report");
        }

        @Override
        public void onMojoExecute(String mojoName, String flag,
                                  String profile, String appShowName,
                                  String appGroup, String message, Throwable throwable) {
            if (skipNotify()) {
                return;
            }
            Map<String, Object> messageMap = new HashMap<String, Object>() {
                {
                    put("profile", profile);
                    put("project", appShowName);
                    put("group", appGroup);
                    put("successFlag", throwable != null ? "Failure" : "Successful");
                    put("mojoName", mojoName);
                    if (message != null && !message.trim().isEmpty()
                            || throwable != null) {
                        put("message", message);
                        put("throwable", throwable);
                    }
                }
            };
            if (throwable != null) {
                doSend(flag, NotifyConfig.TYPE_HTTP, build(PROCESS_EXECUTE_FAILURE_FLAG, messageMap), "DevOps process failure");
            } else {
                doSend(flag, NotifyConfig.TYPE_HTTP, build(PROCESS_EXECUTE_SUCCESS_FLAG, messageMap), "DevOps process successful");
            }

        }

        @Override
        public void onGlobalProcessError(Throwable throwable) {
            if (skipNotify()) {
                return;
            }
            Map<String, Object> message = new HashMap<String, Object>() {
                {
                    put("error", throwable.getMessage() != null ? ": " + throwable.getMessage() : "");
                    put("errorStackTrace", Arrays
                            .stream(throwable.getStackTrace())
                            .map(e -> "> " + e.toString())
                            .limit(20)
                            .collect(Collectors.joining("\n")));
                }
            };
            doSend("", NotifyConfig.TYPE_HTTP, build(PROCESS_GLOBAL_ERROR_FLAG, message), "DevOps process report");
        }

        @Override
        public void onShutdown(Map<String, FinalProjectConfig> projects,
                               List<FinalProjectConfig> executionSuccessfulProjects,
                               List<FinalProjectConfig> nonExecutionProjects) {
            if (skipNotify()) {
                return;
            }
            Map<String, Object> message = new HashMap<>();
            message.put("successfulExecProjects", executionSuccessfulProjects.stream().map(project -> {
                Map<String, Object> result = new HashMap<>();
                result.put("groupId", project.getAppGroup());
                result.put("artifactId", project.getMavenProject().getArtifactId());
                result.put("execMojos", String.join(",", project.getExecuteSuccessfulMojos()));
                result.put("name", project.getAppShowName());
                result.put("reason", null);
                return result;
            }).collect(Collectors.toList()));
            message.put("failureExecProjects", nonExecutionProjects.stream()
                    .filter(project -> !project.isHasError() && !project.getSkip())
                    .map(project -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("groupId", project.getAppGroup());
                        result.put("artifactId", project.getMavenProject().getArtifactId());
                        result.put("execMojos", String.join(",", project.getExecuteSuccessfulMojos()));
                        result.put("name", project.getAppShowName());
                        result.put("reason", project.getSkipReason().isEmpty() ? "unknown error"
                                : project.getSkipReason());
                        return result;
                    }).collect(Collectors.toList()));
            message.put("noneExecProjects", nonExecutionProjects.stream()
                    .filter(project -> !project.isHasError() && !project.getSkip())
                    .map(project -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("groupId", project.getAppGroup());
                        result.put("artifactId", project.getMavenProject().getArtifactId());
                        result.put("execMojos", String.join(",", project.getExecuteSuccessfulMojos()));
                        result.put("name", project.getAppShowName());
                        result.put("reason", project.getSkipReason());
                        return result;
                    }).collect(Collectors.toList()));
            message.put("ignoreExecProjects", projects.values().stream()
                    .filter(project -> !project.isHasError() && project.getSkip())
                    .map(project -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("groupId", project.getAppGroup());
                        result.put("artifactId", project.getMavenProject().getArtifactId());
                        result.put("execMojos", String.join(",", project.getExecuteSuccessfulMojos()));
                        result.put("name", project.getAppShowName());
                        result.put("reason", project.getSkipReason());
                        return result;
                    }).collect(Collectors.toList()));
            doSend("", NotifyConfig.TYPE_HTTP, build(PROCESS_SHUTDOWN_FLAG, message), "DevOps process report");
        }

        private String build(String kind, Map<String, Object> message) {
            return $.json.toJsonString($.json.createObjectNode()
                    .put("kind", kind)
                    .put("ci", appendCIJobUrl())
                    .set("message", $.json.toJson(message)));
        }
    }

}
