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

import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.notification.Notify;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Execute event processor.
 *
 * @author gudaoxuri
 */
public class ExecuteEventProcessor {

    public static void init() {
        // Do nothing
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
        notifyByMojo(mojoName, projectConfig, message, null);
    }

    /**
     * On mojo execute failure.
     *
     * @param mojoName      the mojo name
     * @param projectConfig the project config
     * @param throwable     the throwable
     */
    public static void onMojoExecuteFailure(String mojoName, FinalProjectConfig projectConfig, Throwable throwable) {
        notifyByMojo(mojoName, projectConfig, "", throwable);
    }

    /**
     * On shutdown.
     *
     * @param projects the projects
     */
    public static void onShutdown(Map<String, FinalProjectConfig> projects) {
        if (projects.size() <= 1) {
            // 只有0个或1个项目时不用汇总通知
            return;
        }
        if (!Notify.contains("")) {
            return;
        }
        List<FinalProjectConfig> executionSuccessfulProjects = projects.values().stream()
                .filter(project -> project.getExecuteSuccessfulMojos().contains("release")
                        || project.getExecuteSuccessfulMojos().contains("rollback")
                        || project.getExecuteSuccessfulMojos().contains("scale")
                        || project.getExecuteSuccessfulMojos().contains("unrelease")
                ).collect(Collectors.toList());
        StringBuilder content = new StringBuilder();
        content.append("# The execution result\n")
                .append("-----------------\n")
                .append("## Execute Successful\n");
        content.append(executionSuccessfulProjects.stream()
                .map(project -> "- " + project.getAppShowName())
                .collect(Collectors.joining("\n")));

        List<FinalProjectConfig> nonExecutionProjects =
                projects.values().stream()
                        .filter(project -> !executionSuccessfulProjects.contains(project))
                        .collect(Collectors.toList());
        content.append("\n-----------------\n")
                .append("## Ignore execution\n");
        content.append(nonExecutionProjects.stream()
                .filter(project -> !project.isHasError())
                .map(project -> {
                    String reason = project.getSkipReason();
                    return "- " + project.getAppShowName() + "\n> " + reason;
                })
                .collect(Collectors.joining("\n")));
        content.append("\n-----------------\n")
                .append("## Execute Failure\n");
        content.append(nonExecutionProjects.stream()
                .filter(FinalProjectConfig::isHasError)
                .map(project -> {
                    String reason = project.getSkipReason().isEmpty() ? "unknown error" : project.getSkipReason();
                    return "- " + project.getAppShowName() + "\n> " + reason;
                })
                .collect(Collectors.joining("\n")));
        content.append("\n-----------------\n");
        Notify.send("", content.toString(), "DevOps process report");
    }

    private static void notifyByMojo(String mojoName, FinalProjectConfig projectConfig, String message, Throwable throwable) {
        String flag = projectConfig.getId();
        if (!Notify.contains(flag)) {
            return;
        }
        String content =
                "![](http://dew.ms/images/" + (throwable != null ? "failure" : "successful") + ".png)"
                        + "\n"
                        + "# " + projectConfig.getAppShowName() + "\n"
                        + "> " + projectConfig.getAppGroup() + "\n"
                        + "\n"
                        + "## " + (throwable != null ? "Failure" : "Successful")
                        + " : [" + mojoName + "] @ [" + projectConfig.getProfile() + "]\n";
        if (message != null && !message.trim().isEmpty()
                || throwable != null) {
            content += "> ---------\n"
                    + "> " + message + "\n"
                    + "> " + throwable + "\n";
        }
        if (throwable != null) {
            Notify.send(flag, content, "DevOps process failure");
        } else {
            Notify.send(flag, content, "DevOps process successful");
        }
    }


}
