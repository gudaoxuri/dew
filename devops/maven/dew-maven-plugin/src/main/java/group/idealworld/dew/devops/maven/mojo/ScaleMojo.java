package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Scale mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "scale")
public class ScaleMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        if (!autoScale && replicas == 0) {
            logger.error("Parameter error, When autoScale disabled, dew_devops_scale_replicas can't be 0");
            return false;
        }
        if (autoScale && (minReplicas == 0 || maxReplicas == 0 || minReplicas >= maxReplicas || cpuAvg == 0)) {
            logger.error("Parameter error, Current mode is autoScale model");
            return false;
        }
        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .scaleFlow(replicas, autoScale, minReplicas, maxReplicas, cpuAvg)
                .exec(mavenProject.getId(), getMojoName());
    }

}
