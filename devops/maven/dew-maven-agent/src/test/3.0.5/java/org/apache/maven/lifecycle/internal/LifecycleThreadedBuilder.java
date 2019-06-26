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

package org.apache.maven.lifecycle.internal;

import ms.dew.devops.agent.SkipCheck;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Builds the full lifecycle in weave-mode (phase by phase as opposed to project-by-project)
 * 
 * @since 3.0
 * @author Kristian Rosenvold
 *         Builds one or more lifecycles for a full module
 *         <p/>
 *         NOTE: This class is not part of any public api and can be changed or deleted without prior notice.
 */
@Component( role = LifecycleThreadedBuilder.class )
public class LifecycleThreadedBuilder
{

    @Requirement
    private Logger logger;

    @Requirement
    private LifecycleModuleBuilder lifecycleModuleBuilder;


    @SuppressWarnings( { "UnusedDeclaration" } )
    public LifecycleThreadedBuilder()
    {
    }

    public void build( MavenSession session, ReactorContext reactorContext, ProjectBuildList projectBuilds,
                       List<TaskSegment> currentTaskSegment, ConcurrencyDependencyGraph analyzer,
                       CompletionService<ProjectSegment> service )
    {

        // Currently disabled
        ThreadOutputMuxer muxer = null; // new ThreadOutputMuxer( analyzer.getProjectBuilds(), System.out );

        for ( TaskSegment taskSegment : currentTaskSegment )
        {
            Map<MavenProject, ProjectSegment> projectBuildMap = projectBuilds.selectSegment( taskSegment );
                try
                {
                multiThreadedProjectTaskSegmentBuild( analyzer, reactorContext, session, service, taskSegment,
                                                      projectBuildMap, muxer );
                    if ( reactorContext.getReactorBuildStatus().isHalted( ) )
                    {
                        break;
                    }
                }
                catch ( Exception e )
                {
                    break;  // Why are we just ignoring this exception? Are exceptions are being used for flow control
                }

        }
    }

    private void multiThreadedProjectTaskSegmentBuild( ConcurrencyDependencyGraph analyzer,
                                                       ReactorContext reactorContext, MavenSession rootSession,
                                                       CompletionService<ProjectSegment> service,
                                                       TaskSegment taskSegment,
                                                       Map<MavenProject, ProjectSegment> projectBuildList,
                                                       ThreadOutputMuxer muxer )
    {

        // schedule independent projects
        for ( MavenProject mavenProject : analyzer.getRootSchedulableBuilds() )
        {
            if (SkipCheck.skip(mavenProject.getBasedir())) {
                continue;
            }
            ProjectSegment projectSegment = projectBuildList.get( mavenProject );
            logger.debug( "Scheduling: " + projectSegment.getProject() );
            Callable<ProjectSegment> cb =
                createBuildCallable( rootSession, projectSegment, reactorContext, taskSegment, muxer );
            service.submit( cb );
        }

        // for each finished project
        for ( int i = 0; i < analyzer.getNumberOfBuilds(); i++ )
        {
            try
            {
                ProjectSegment projectBuild = service.take().get();
                if ( reactorContext.getReactorBuildStatus().isHalted() )
                {
                    break;
                }
                final List<MavenProject> newItemsThatCanBeBuilt =
                    analyzer.markAsFinished( projectBuild.getProject() );
                for ( MavenProject mavenProject : newItemsThatCanBeBuilt )
                {
                    ProjectSegment scheduledDependent = projectBuildList.get( mavenProject );
                    logger.debug( "Scheduling: " + scheduledDependent );
                    Callable<ProjectSegment> cb =
                        createBuildCallable( rootSession, scheduledDependent, reactorContext, taskSegment, muxer );
                    service.submit( cb );
                }
            }
            catch ( InterruptedException e )
            {
                break;
            }
            catch ( ExecutionException e )
            {
                break;
            }
        }

        // cancel outstanding builds (if any)  - this can happen if an exception is thrown in above block

        Future<ProjectSegment> unprocessed;
        while ( ( unprocessed = service.poll() ) != null )
        {
            try
            {
                unprocessed.get();
            }
            catch ( InterruptedException e )
            {
                throw new RuntimeException( e );
            }
            catch ( ExecutionException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    private Callable<ProjectSegment> createBuildCallable( final MavenSession rootSession,
                                                          final ProjectSegment projectBuild,
                                                          final ReactorContext reactorContext,
                                                          final TaskSegment taskSegment, final ThreadOutputMuxer muxer )
    {
        return new Callable<ProjectSegment>()
        {
            public ProjectSegment call()
            {
                // muxer.associateThreadWithProjectSegment( projectBuild );
                lifecycleModuleBuilder.buildProject( projectBuild.getSession(), rootSession, reactorContext,
                                                     projectBuild.getProject(), taskSegment );
                // muxer.setThisModuleComplete( projectBuild );

                return projectBuild;
            }
        };
    }
}