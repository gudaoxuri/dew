package group.idealworld.dew.devops.kernel.flow.unrelease;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;

/**
 * Default un-release flow.
 *
 * @author gudaoxuri
 */
public class DefaultUnReleaseFlow extends BasicFlow {

    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        // 删除 service
        KubeHelper.inst(config.getId()).delete(config.getAppName(), config.getNamespace(), KubeRES.SERVICE);
        // 删除 Deployment,ReplicaSet,Pod
        KubeHelper.inst(config.getId()).delete(config.getAppName(), config.getNamespace(), KubeRES.DEPLOYMENT);
    }

}
