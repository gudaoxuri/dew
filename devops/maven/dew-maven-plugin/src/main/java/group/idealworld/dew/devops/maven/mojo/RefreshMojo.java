package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Default refresh flow.
 *
 * @author Sun
 */
@Mojo(name = "refresh")
public class RefreshMojo extends BasicMojo {
    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .refreshFlow().exec(mavenProject.getId(), getMojoName());
    }
}
