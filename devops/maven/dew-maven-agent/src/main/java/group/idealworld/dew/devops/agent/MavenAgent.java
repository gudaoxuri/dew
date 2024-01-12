package group.idealworld.dew.devops.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;

/**
 * Agent Launcher.
 *
 * @author gudaoxuri
 */
public class MavenAgent {

    /**
     * Premain.
     *
     * @param agentArgs the agent args
     * @param inst      the inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        String mavenHome = System.getProperty("maven.home");
        if (mavenHome == null || mavenHome.isEmpty()) {
            throw new RuntimeException("Can't find [maven.home] configuration in system property");
        }
        File mavenHomePath = new File(mavenHome);
        File mavenLibPath = new File(mavenHomePath.getPath() + File.separator + "lib");
        File mavenCoreFile = mavenLibPath.listFiles(pathname -> pathname.getName().contains("maven-core-"))[0];
        switch (mavenCoreFile.getName()) {
            case "maven-core-3.6.0.jar":
                inst.addTransformer(new Transformer("3.6.0", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.6.1.jar":
                inst.addTransformer(new Transformer("3.6.1", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.5.2.jar":
                inst.addTransformer(new Transformer("3.5.2", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.3.9.jar":
                inst.addTransformer(new Transformer("3.3.9", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.3.3.jar":
                inst.addTransformer(new Transformer("3.3.3", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.2.5.jar":
                inst.addTransformer(new Transformer("3.2.5", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.0.5.jar":
                inst.addTransformer(new Transformer("3.0.5", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/LifecycleStarter");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            default:
                throw new RuntimeException("Unsupported versions : " + mavenCoreFile.getName());
        }

    }

}
