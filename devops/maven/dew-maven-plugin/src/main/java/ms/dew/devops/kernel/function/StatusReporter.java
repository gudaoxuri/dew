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
import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.util.DewLog;
import ms.dew.devops.kernel.util.ExecuteOnceProcessor;
import org.slf4j.Logger;

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
            String msg = DevOps.Config.getFinalConfig().getProjects().values().stream()
                    .filter(config -> !config.getSkip())
                    .map(config -> {
                        String status = "";
                        if (config.getExecuteSuccessfulMojos().contains("release")) {
                            status += "[*]";
                        } else {
                            status += "[ ]";
                        }
                        status += String.format(" %-30s | ", config.getAppShowName());
                        status += String.join(" > ", config.getExecuteSuccessfulMojos());
                        return status;
                    })
                    .collect(Collectors.joining("\n", "Status Report:\n----------------------\n", "\n----------------------\n\n"));
            logger.info(msg);
        });
    }
}
