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
