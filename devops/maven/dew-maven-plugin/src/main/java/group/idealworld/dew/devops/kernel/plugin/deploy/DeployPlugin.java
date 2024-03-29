package group.idealworld.dew.devops.kernel.plugin.deploy;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * 部署插件定义.
 *
 * @author gudaoxuri
 */
public interface DeployPlugin {

    /**
     * 是否可以部署判断.
     *
     * @param projectConfig the project config
     * @return the resp
     */
    Resp<String> deployAble(FinalProjectConfig projectConfig);

    /**
     * 获取部署的最新的版本.
     *
     * @param projectId the project id
     * @param appName   the app name
     * @param namespace the namespace
     * @return the optional
     */
    Optional<String> fetchLastDeployedVersion(String projectId, String appName, String namespace);

    /**
     * 获取重用环境部署的最新的版本.
     *
     * @param projectConfig the project config
     * @return 最新的版本
     * @throws IOException the IOException
     */
    Optional<String> fetchLastDeployedVersionByReuseProfile(FinalProjectConfig projectConfig) throws IOException;

    /**
     * 获取环境变量.
     *
     * @param projectConfig the project config
     * @return the env
     */
    Map<String, String> getEnv(FinalProjectConfig projectConfig);

    /**
     * 是否使用Maven自身的处理机制.
     * <p>
     * 比如自身的 install deploy 方式
     *
     * @return the result
     */
    boolean useMavenProcessingMode();

}
