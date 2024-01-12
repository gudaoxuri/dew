package group.idealworld.dew.devops.maven.function;

import group.idealworld.dew.devops.kernel.exception.GlobalProcessException;
import group.idealworld.dew.devops.kernel.util.ExecuteOnceProcessor;
import org.apache.maven.DefaultProjectDependenciesResolver;
import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.MultipleArtifactsNotFoundException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 依赖发现.
 *
 * @author gudaoxuri
 */
public class DependenciesResolver {

    /**
     * Init.
     *
     * @param mavenSession the maven session
     */
    public static void init(MavenSession mavenSession) {
        if (ExecuteOnceProcessor.executedCheck(DependenciesResolver.class)) {
            return;
        }
        try {
            DefaultProjectDependenciesResolver resolver = (DefaultProjectDependenciesResolver) mavenSession
                    .getContainer()
                    .lookup(ProjectDependenciesResolver.class.getName());
            for (MavenProject mavenProject : mavenSession.getProjectDependencyGraph().getSortedProjects()) {
                mavenProject.setArtifacts(resolve(resolver, mavenProject, mavenSession));
            }
        } catch (ComponentLookupException e) {
            throw new GlobalProcessException(e.getMessage(), e);
        }
    }

    private static Set<Artifact> resolve(DefaultProjectDependenciesResolver resolver,
            MavenProject mavenProject, MavenSession mavenSession) {
        try {
            return resolver.resolve(mavenProject, Collections.singleton(Artifact.SCOPE_COMPILE), mavenSession);
        } catch (MultipleArtifactsNotFoundException e) {
            Set<Artifact> result = new HashSet<>();
            for (Artifact missArtifact : e.getMissingArtifacts()) {
                Optional<MavenProject> innerProjectOpt = mavenSession.getProjects()
                        .stream().filter(project -> project.getId().equalsIgnoreCase(missArtifact.getId()))
                        .findAny();
                if (innerProjectOpt.isPresent()) {
                    result.addAll(innerProjectOpt.get().getArtifacts());
                } else {
                    throw new GlobalProcessException(e.getMessage(), e);
                }
            }
            try {
                result.addAll(resolver.resolve(mavenProject, null,
                        Collections.singleton(Artifact.SCOPE_COMPILE), mavenSession,
                        new HashSet<>(e.getMissingArtifacts())));
                return result;
            } catch (ArtifactResolutionException | ArtifactNotFoundException ex) {
                throw new GlobalProcessException(ex.getMessage(), ex);
            }
        } catch (ArtifactNotFoundException | ArtifactResolutionException e) {
            throw new GlobalProcessException(e.getMessage(), e);
        }
    }

}
