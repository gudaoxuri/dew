package group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_non_natvie;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.release.DockerBuildFlow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Node frontend build flow.
 *
 * @author gudaoxuri
 */
public class FrontendNonNativeNodeBuildFlow extends DockerBuildFlow {

    protected void preDockerBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        if (config.getApp().getServerConfig() != null && !config.getApp().getServerConfig().isEmpty()) {
            Files.write(Paths.get(flowBasePath + "custom.conf"), config.getApp().getServerConfig().getBytes());
        } else {
            Files.write(Paths.get(flowBasePath + "custom.conf"), "".getBytes());
        }
        $.file.copyStreamToPath(DevOps.class.getResourceAsStream("/dockerfile/frontend/node_non_native/Dockerfile"),
                flowBasePath + "Dockerfile");
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
