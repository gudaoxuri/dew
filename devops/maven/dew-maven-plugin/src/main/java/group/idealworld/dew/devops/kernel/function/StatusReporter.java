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
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.util.DewLog;
import group.idealworld.dew.devops.kernel.util.ExecuteOnceProcessor;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 发布状态报告.
 * <p>
 * 在使用并发编译时可以更清楚地观察执行情况
 *
 * @author gudaoxuri
 */
public class StatusReporter {

    private static Logger logger = DewLog.build(StatusReporter.class);

    /**
     * Report.
     */
    public static void report() {
        if (ExecuteOnceProcessor.executedCheck(StatusReporter.class)) {
            return;
        }
        $.timer.periodic(5, 30, true, () -> {
            List<FinalProjectConfig> processProjects = DevOps.Config.getFinalConfig().getProjects().values().stream()
                    .filter(config -> !config.getSkip())
                    .collect(Collectors.toList());
            List<String> finishedProjects = processProjects.stream()
                    .filter(projectConfig -> projectConfig.getExecuteSuccessfulMojos().contains("release"))
                    .map(FinalProjectConfig::getAppShowName)
                    .collect(Collectors.toList());
            List<String> processingProjects = processProjects.stream()
                    .filter(projectConfig -> !projectConfig.getExecuteSuccessfulMojos().contains("release"))
                    .map(FinalProjectConfig::getAppShowName)
                    .collect(Collectors.toList());
            String msg = "\nStatus Report ["
                    + finishedProjects.size() + "/" + processProjects.size()
                    + "]\n----------------------\n";
            msg += "\n >>>> Finished <<<<\n" + String.join(",", finishedProjects);
            msg += "\n >>>> Processing <<<<\n" + String.join(",", processingProjects);
            msg += "\n----------------------\n\n";
            logger.info(msg);
        });
    }
}
