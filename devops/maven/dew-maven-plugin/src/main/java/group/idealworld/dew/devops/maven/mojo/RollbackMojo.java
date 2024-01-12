package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Rollback mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "rollback")
public class RollbackMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .rollbackFlow(history, rollbackVersion).exec(mavenProject.getId(), getMojoName());
    }

}
