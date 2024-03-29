package org.apache.maven.lifecycle.internal;

import group.idealworld.dew.devops.agent.SkipCheck;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.DefaultLifecycles;
import org.apache.maven.lifecycle.MissingProjectException;
import org.apache.maven.lifecycle.NoGoalSpecifiedException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Starts the build life cycle
 * 
 * @author Jason van Zyl
 * @author Benjamin Bentmann
 * @author Kristian Rosenvold
 */
@Component(role = LifecycleStarter.class)
public class LifecycleStarter {

    @Requirement
    private ExecutionEventCatapult eventCatapult;

    @Requirement
    private DefaultLifecycles defaultLifeCycles;

    @Requirement
    private Logger logger;

    @Requirement
    private LifecycleModuleBuilder lifecycleModuleBuilder;

    @Requirement
    private LifecycleWeaveBuilder lifeCycleWeaveBuilder;

    @Requirement
    private LifecycleThreadedBuilder lifecycleThreadedBuilder;

    @Requirement
    private BuildListCalculator buildListCalculator;

    @Requirement
    private LifecycleDebugLogger lifecycleDebugLogger;

    @Requirement
    private LifecycleTaskSegmentCalculator lifecycleTaskSegmentCalculator;

    @Requirement
    private ThreadConfigurationService threadConfigService;

    public void execute(MavenSession session) {
        eventCatapult.fire(ExecutionEvent.Type.SessionStarted, session, null);

        MavenExecutionResult result = session.getResult();

        try {
            if (!session.isUsingPOMsFromFilesystem() && lifecycleTaskSegmentCalculator.requiresProject(session)) {
                throw new MissingProjectException("The goal you specified requires a project to execute"
                        + " but there is no POM in this directory (" + session.getExecutionRootDirectory() + ")."
                        + " Please verify you invoked Maven from the correct directory.");
            }

            final MavenExecutionRequest executionRequest = session.getRequest();
            boolean isThreaded = executionRequest.isThreadConfigurationPresent();
            session.setParallel(isThreaded);

            List<TaskSegment> taskSegments = lifecycleTaskSegmentCalculator.calculateTaskSegments(session);

            ProjectBuildList projectBuilds = buildListCalculator.calculateProjectBuilds(session, taskSegments);

            if (projectBuilds.isEmpty()) {
                throw new NoGoalSpecifiedException("No goals have been specified for this build."
                        + " You must specify a valid lifecycle phase or a goal in the format <plugin-prefix>:<goal> or"
                        + " <plugin-group-id>:<plugin-artifact-id>[:<plugin-version>]:<goal>."
                        + " Available lifecycle phases are: " + defaultLifeCycles.getLifecyclePhaseList() + ".");
            }

            ProjectIndex projectIndex = new ProjectIndex(session.getProjects());

            if (logger.isDebugEnabled()) {
                lifecycleDebugLogger.debugReactorPlan(projectBuilds);
            }

            ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();

            ReactorBuildStatus reactorBuildStatus = new ReactorBuildStatus(session.getProjectDependencyGraph());
            ReactorContext callableContext = new ReactorContext(result, projectIndex, oldContextClassLoader,
                    reactorBuildStatus);

            if (isThreaded) {
                ExecutorService executor = threadConfigService.getExecutorService(executionRequest.getThreadCount(),
                        executionRequest.isPerCoreThreadCount(),
                        session.getProjects().size());
                try {

                    final boolean isWeaveMode = LifecycleWeaveBuilder.isWeaveMode(executionRequest);
                    if (isWeaveMode) {
                        lifecycleDebugLogger.logWeavePlan(session);
                        lifeCycleWeaveBuilder.build(projectBuilds, callableContext, taskSegments, session, executor,
                                reactorBuildStatus);
                    } else {
                        ConcurrencyDependencyGraph analyzer = new ConcurrencyDependencyGraph(projectBuilds,
                                session.getProjectDependencyGraph());

                        CompletionService<ProjectSegment> service = new ExecutorCompletionService<ProjectSegment>(
                                executor);

                        lifecycleThreadedBuilder.build(session, callableContext, projectBuilds, taskSegments, analyzer,
                                service);
                    }
                } finally {
                    executor.shutdown();
                    // If the builder has terminated with an exception we want to catch any stray
                    // threads before going
                    // to System.exit in the mavencli.
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                }
            } else {
                singleThreadedBuild(session, callableContext, projectBuilds, taskSegments, reactorBuildStatus);
            }

        } catch (Exception e) {
            result.addException(e);
        }

        eventCatapult.fire(ExecutionEvent.Type.SessionEnded, session, null);
    }

    private void singleThreadedBuild(MavenSession session, ReactorContext callableContext,
            ProjectBuildList projectBuilds, List<TaskSegment> taskSegments,
            ReactorBuildStatus reactorBuildStatus) {
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
                    lifecycleModuleBuilder.buildProject(session, callableContext, projectBuild.getProject(),
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
