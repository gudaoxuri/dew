package group.idealworld.dew.devops.kernel.plugin.appkind;

import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

import java.util.Map;

/**
 * App类型插件定义.
 *
 * @author gudaoxuri
 */
public interface AppKindPlugin {

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Custom config.
     *
     * @param projectConfig the project config
     */
    void customConfig(FinalProjectConfig projectConfig);

    /**
     * Prepare flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow prepareFlow();

    /**
     * Build flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow buildFlow();

    /**
     * Release flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow releaseFlow();

    /**
     * UnRelease flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow unReleaseFlow();

    /**
     * Rollback flow basic flow.
     *
     * @param history the history
     * @param version the version
     * @return the basic flow
     */
    BasicFlow rollbackFlow(boolean history, String version);

    /**
     * Scale flow basic flow.
     *
     * @param replicas    the replicas
     * @param autoScale   the auto scale
     * @param minReplicas the min replicas
     * @param maxReplicas the max replicas
     * @param cpuAvg      the cpu avg
     * @return the basic flow
     */
    BasicFlow scaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg);

    /**
     * Refresh flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow refreshFlow();

    /**
     * Log flow basic flow.
     *
     * @param podName the pod name
     * @param follow  the follow
     * @return the basic flow
     */
    BasicFlow logFlow(String podName, boolean follow);

    /**
     * Debug flow basic flow.
     *
     * @param podName     the pod name
     * @param forwardPort the forward port
     * @return the basic flow
     */
    BasicFlow debugFlow(String podName, int forwardPort);

    /**
     * 获取环境变量.
     *
     * @param projectConfig the project config
     * @return the env
     */
    Map<String, String> getEnv(FinalProjectConfig projectConfig);

}
