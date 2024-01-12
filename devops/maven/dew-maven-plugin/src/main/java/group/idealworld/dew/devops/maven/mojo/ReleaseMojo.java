package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.function.StatusReporter;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

/**
 * Release mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "release", defaultPhase = LifecyclePhase.DEPLOY, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class ReleaseMojo extends BasicMojo {

        @Override
        protected boolean executeInternal() throws IOException, ApiException {
                if (mavenSession.isParallel()) {
                        StatusReporter.report();
                }
                DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                                .prepareFlow().exec(mavenProject.getId(), getMojoName());

                DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                                .buildFlow().exec(mavenProject.getId(), getMojoName());

                return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                                .releaseFlow().exec(mavenProject.getId(), getMojoName());
        }
}
