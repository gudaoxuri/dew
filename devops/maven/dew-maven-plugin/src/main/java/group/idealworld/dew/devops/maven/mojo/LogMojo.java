package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Log view mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "log")
public class LogMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .logFlow(podName, follow).exec(mavenProject.getId(), getMojoName());
    }

}
