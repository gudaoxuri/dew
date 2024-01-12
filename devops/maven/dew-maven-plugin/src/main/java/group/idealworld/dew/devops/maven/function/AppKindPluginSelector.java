package group.idealworld.dew.devops.maven.function;

import group.idealworld.dew.devops.kernel.exception.ConfigException;
import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_native.FrontendNativeNodeAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_non_natvie.FrontendNonNativeNodeAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.jvmlib.JvmLibAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.jvmservice_springboot.JvmServiceSpringBootAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.pom.PomAppKindPlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * App类型插件选择器.
 *
 * @author gudaoxuri
 */
public class AppKindPluginSelector {

    /**
     * Select app kind plugin.
     *
     * @param mavenProject the maven project
     * @return the optional
     */
    public static Optional<AppKindPlugin> select(MavenProject mavenProject) {
        if (mavenProject.getPackaging().equalsIgnoreCase("maven-plugin")) {
            return Optional.of(new JvmLibAppKindPlugin());
        }
        if (new File(mavenProject.getBasedir().getPath() + File.separator + "package.json").exists()) {
            if (StringUtils.isNotBlank(mavenProject.getProperties().getProperty("frontend.package.type"))
                    && mavenProject.getProperties().getProperty("frontend.package.type").equals("NATIVE")) {
                return Optional.of(new FrontendNativeNodeAppKindPlugin());
            }
            return Optional.of(new FrontendNonNativeNodeAppKindPlugin());
        }
        if (mavenProject.getPackaging().equalsIgnoreCase("jar")
                && new File(mavenProject.getBasedir().getPath() + File.separator
                        + "src" + File.separator
                        + "main" + File.separator
                        + "resources").exists()
                && Arrays.stream(new File(mavenProject.getBasedir().getPath() + File.separator
                        + "src" + File.separator
                        + "main" + File.separator
                        + "resources").listFiles())
                        .anyMatch((res -> res.getName().toLowerCase().contains("application")
                                || res.getName().toLowerCase().contains("bootstrap")))
                && mavenProject.getArtifacts().stream()
                        .map(artifact -> artifact.getGroupId() + ":" + artifact.getArtifactId())
                        .anyMatch("org.springframework.boot:spring-boot-starter-web"::equalsIgnoreCase)) {
            return Optional.of(new JvmServiceSpringBootAppKindPlugin());
        }
        if (mavenProject.getPackaging().equalsIgnoreCase("jar")) {
            return Optional.of(new JvmLibAppKindPlugin());
        }
        if (mavenProject.getPackaging().equalsIgnoreCase("pom")) {
            return Optional.of(new PomAppKindPlugin());
        }
        throw new ConfigException("The project [" + mavenProject.getId() + "] kind does not supported");
    }

}
