package group.idealworld.dew.devops.kernel.flow.release;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.function.VersionController;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Maven release flow.
 * <p>
 * Maven的部署由Maven自身实现，pom.xml中需要配置 distributionManagement
 *
 * @author gudaoxuri
 */
public class MavenReleaseFlow extends BasicFlow {

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        VersionController.addNewVersion(config, config.getAppVersion(), config.getGitCommit(), false, new HashMap<>(),
                new HashMap<>());
        List<V1ConfigMap> versions = VersionController.getVersionHistory(config.getId(), config.getAppName(),
                config.getNamespace(), false);
        for (V1ConfigMap version : versions) {
            String appVersion = VersionController.getAppVersion(version);
            // Maven项目只需要保留一个最新版本
            if (!appVersion.equalsIgnoreCase(config.getAppVersion())) {
                VersionController.deleteVersion(config, appVersion);
            }
        }
    }

}
