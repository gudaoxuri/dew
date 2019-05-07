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

package ms.dew.devops.kernel.function;

import com.ecfront.dew.common.$;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.notification.Notify;
import ms.dew.notification.NotifyConfig;

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

    /**
     * Init.
     *
     * @param processingProjects the processing projects
     */
    public static void init(List<FinalProjectConfig> processingProjects) {
        if (!Notify.contains("")) {
            return;
        }
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
        if (!Notify.contains("")) {
            return;
        }
        SendFactory.onGlobalProcessError(throwable);
    }

    /**
     * On shutdown.
     *
     * @param projects the projects
     */
    public static void onShutdown(Map<String, FinalProjectConfig> projects) {
        if (Dew.stopped) {
            return;
        }
        if (!Notify.contains("")) {
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
        public void initWithEmpty() {
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.ms/images/devops-notify/report.png)")
                    .append("\n")
                    .append("No projects need to be processed.")
                    .append("\n\n")
                    .append(appendCIJobUrl());
            doSend("", NotifyConfig.TYPE_DD, content.toString(), "DevOps process report");
        }

        @Override
        public void init(String profile, List<String> appShowNames) {
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.ms/images/devops-notify/report.png)")
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
            if (!Notify.contains(flag + "_" + NotifyConfig.TYPE_DD)) {
                return;
            }
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.ms/images/devops-notify/" + (throwable != null ? "failure" : "successful") + ".png)")
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
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.ms/images/devops-notify/report.png)")
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
            StringBuilder content = new StringBuilder();
            content.append("![](http://doc.dew.ms/images/devops-notify/report.png)")
                    .append("\n")
                    .append("## Execute Successful\n")
                    .append("![](http://doc.dew.ms/images/devops-notify/successful-split.png)")
                    .append("\n");
            content.append(executionSuccessfulProjects.stream()
                    .map(project -> "- " + project.getAppShowName())
                    .collect(Collectors.joining("\n")));

            content.append("\n\n")
                    .append("## Execute Failure\n")
                    .append("![](http://doc.dew.ms/images/devops-notify/failure-split.png)")
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
                    .append("![](http://doc.dew.ms/images/devops-notify/non-split.png)")
                    .append("\n");
            content.append(nonExecutionProjects.stream()
                    .filter(project -> !project.isHasError() && !project.isSkip())
                    .map(project -> "- " + project.getAppShowName() + "\n> " + project.getSkipReason())
                    .collect(Collectors.joining("\n")));
            content.append("\n\n")
                    .append("## Ignore execution\n")
                    .append("![](http://doc.dew.ms/images/devops-notify/ignore-split.png)")
                    .append("\n");
            content.append(projects.values().stream()
                    .filter(project -> !project.isHasError() && project.isSkip())
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
        public void initWithEmpty() {
            doSend("", NotifyConfig.TYPE_HTTP, build(PROCESS_EMPTY_FLAG, new HashMap<>()), "DevOps process report");
        }

        @Override
        public void init(String profile, List<String> appShowNames) {
            Map<String, Object> message = new HashMap<String, Object>() {
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
            if (!Notify.contains(flag + "_" + NotifyConfig.TYPE_HTTP)) {
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
            Map<String, Object> message = new HashMap<String, Object>() {
                {
                    put("successfulExecProjects", executionSuccessfulProjects.stream().map(project -> {
                        Map<String, String> result = new HashMap<String, String>() {
                            {
                                put("project", project.getAppShowName());
                                put("reason", null);
                            }
                        };
                        return result;
                    }).collect(Collectors.toList()));
                    put("failureExecProjects", nonExecutionProjects.stream().filter(FinalProjectConfig::isHasError)
                            .map(project -> {
                                Map<String, String> result = new HashMap<String, String>() {
                                    {
                                        put("project", project.getAppShowName());
                                        put("reason", project.getSkipReason().isEmpty() ? "unknown error"
                                                : project.getSkipReason());
                                    }
                                };
                                return result;
                            }).collect(Collectors.toList()));
                    put("noneExecProjects", nonExecutionProjects.stream()
                            .filter(project -> !project.isHasError() && !project.isSkip())
                            .map(project -> {
                                Map<String, String> result = new HashMap<String, String>() {
                                    {
                                        put("project", project.getAppShowName());
                                        put("reason", project.getSkipReason());
                                    }
                                };
                                return result;
                            }).collect(Collectors.toList()));
                    put("ignoreExecProjects", projects.values().stream()
                            .filter(project -> !project.isHasError() && project.isSkip())
                            .map(project -> {
                                Map<String, String> result = new HashMap<String, String>() {
                                    {
                                        put("project", project.getAppShowName());
                                        put("reason", project.getSkipReason());
                                    }
                                };
                                return result;
                            }).collect(Collectors.toList()));
                }
            };
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
