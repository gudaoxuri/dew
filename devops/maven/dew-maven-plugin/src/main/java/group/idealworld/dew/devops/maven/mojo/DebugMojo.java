package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Debug mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "debug")
public class DebugMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .debugFlow(podName, forwardPort).exec(mavenProject.getId(), getMojoName());
    }

}
