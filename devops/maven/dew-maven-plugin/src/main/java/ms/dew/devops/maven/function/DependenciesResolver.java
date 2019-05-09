/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.dew.devops.maven.function;

import ms.dew.devops.kernel.exception.GlobalProcessException;
import ms.dew.devops.kernel.util.ExecuteOnceProcessor;
import org.apache.maven.DefaultProjectDependenciesResolver;
import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collections;
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
            DefaultProjectDependenciesResolver resolver = (DefaultProjectDependenciesResolver) mavenSession.getContainer()
                    .lookup(ProjectDependenciesResolver.class.getName());
            for (MavenProject mavenProject : mavenSession.getProjectDependencyGraph().getSortedProjects()) {
                Set<Artifact> result = resolver.resolve(mavenProject, Collections.singleton(Artifact.SCOPE_COMPILE), mavenSession);
                mavenProject.setArtifacts(result);
            }
        } catch (ArtifactResolutionException | ArtifactNotFoundException | ComponentLookupException e) {
            throw new GlobalProcessException(e.getMessage(), e);
        }
    }

}
