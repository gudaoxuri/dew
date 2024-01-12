package group.idealworld.dew.devops.kernel.plugin.appkind.jvmservice_springboot;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.release.DockerBuildFlow;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring boot build flow.
 *
 * @author gudaoxuri
 */
public class JvmServiceSpringBootBuildFlow extends DockerBuildFlow {

    protected void preDockerBuild(FinalProjectConfig config, String flowBasePath) {
        $.file.copyStreamToPath(DevOps.class.getResourceAsStream("/dockerfile/jvmservice_springboot/Dockerfile"),
                flowBasePath + "Dockerfile");
        $.file.copyStreamToPath(DevOps.class.getResourceAsStream("/dockerfile/jvmservice_springboot/run-java.sh"),
                flowBasePath + "run-java.sh");
        $.file.copyStreamToPath(DevOps.class.getResourceAsStream("/dockerfile/jvmservice_springboot/debug-java.sh"),
                flowBasePath + "debug-java.sh");
        $.file.copyStreamToPath(DevOps.class.getResourceAsStream("/dockerfile/jvmservice_springboot/debug-clear.sh"),
                flowBasePath + "debug-clear.sh");
    }

    @Override
    protected Map<String, String> packageDockerFileArg(FinalProjectConfig config) {
        return new HashMap<>() {
            {
                put("PORT", config.getApp().getPort() + "");
            }
        };
    }

}
