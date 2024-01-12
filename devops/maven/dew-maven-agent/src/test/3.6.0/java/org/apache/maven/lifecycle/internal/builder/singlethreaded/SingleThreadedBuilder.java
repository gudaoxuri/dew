package org.apache.maven.lifecycle.internal.builder.singlethreaded;

import group.idealworld.dew.devops.agent.SkipCheck;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.internal.*;
import org.apache.maven.lifecycle.internal.builder.Builder;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.List;

/**
 * <p>
 * A {@link Builder} encapsulates a strategy for building a set of Maven
 * projects. The default strategy in Maven builds
 * the the projects serially, but a {@link Builder} can employ any type of
 * concurrency model to build the projects.
 */
@Component(role = Builder.class, hint = "singlethreaded")
public class SingleThreadedBuilder
        implements Builder {
    @Requirement
    private LifecycleModuleBuilder lifecycleModuleBuilder;

    public void build(MavenSession session, ReactorContext reactorContext, ProjectBuildList projectBuilds,
            List<TaskSegment> taskSegments, ReactorBuildStatus reactorBuildStatus) {
        for (TaskSegment taskSegment : taskSegments) {
            for (ProjectSegment projectBuild : projectBuilds.getByTaskSegment(taskSegment)) {
                try {
                    if (null != session.getCurrentProject() && SkipCheck.skip(session.getCurrentProject().getBasedir())
                            && session.getGoals().stream().map(String::toLowerCase)
                                    .anyMatch(s -> s.contains("group.idealworld.dew:dew-maven-plugin:release")
                                            || s.contains("dew:release")
                                            || s.contains("deploy"))) {
                        continue;
                    }
                    lifecycleModuleBuilder.buildProject(session, reactorContext, projectBuild.getProject(),
                            taskSegment);
                    if (reactorBuildStatus.isHalted()) {
                        break;
                    }
                } catch (Exception e) {
                    break; // Why are we just ignoring this exception? Are exceptions are being used for
                           // flow control
                }
            }
        }
    }
}
