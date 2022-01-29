/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.sdkgen.process;

import com.ecfront.dew.common.tuple.Tuple3;
import group.idealworld.dew.sdkgen.helper.MavenHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;

import static group.idealworld.dew.sdkgen.Constants.GENERATED_BASE_PATH;

/**
 * The type Release process.
 *
 * @author gudaoxuri
 */
@Slf4j
public class SDKReleaseProcess {

    /**
     * Process.
     *
     * @param sdkMavenInfo  the sdk maven info
     * @param mavenProject  the maven project
     * @param mavenSession  the maven session
     * @param pluginManager the plugin manager
     */
    @SneakyThrows
    public static void process(Tuple3<String, String, String> sdkMavenInfo,
                               MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager) {
        var basePath = mavenProject.getBasedir().getPath() + File.separator +
                "target" + File.separator +
                GENERATED_BASE_PATH;
        var distributionRepository = mavenProject.getDistributionManagementArtifactRepository();
        var distributionRepId = distributionRepository.getId();
        var distributionRepUrl = distributionRepository.getUrl();
        log.info("Deploy SDK from : {} to [{}]{}", basePath, distributionRepId, distributionRepUrl);
        MavenHelper.invoke("org.apache.maven.plugins", "maven-invoker-plugin", null,
                "run", new HashMap<>() {
                    {
                        put("projectsDirectory", basePath);
                        put("goals", new HashMap<>() {
                            {
                                put("goal", "deploy");
                            }
                        });
                        put("showErrors", true);
                        put("streamLogs", true);
                        put("mavenOpts", "-DaltDeploymentRepository=" + distributionRepId + "::default::" + distributionRepUrl);
                    }
                }, mavenProject, mavenSession, pluginManager);
        // 删除临时文件
        log.debug("Delete the SDK temporary build directory {}", basePath);
        FileUtils.deleteDirectory(new File(basePath));
        log.info("\n========================\n" +
                "The SDK deployment is complete and the following dependencies can be added to the maven.\n" +
                "\n" +
                "    <groupId>{}</groupId>\n" +
                "    <artifactId>{}</artifactId>\n" +
                "    <version>{}</version>\n" +
                "\n========================", sdkMavenInfo._0, sdkMavenInfo._1, sdkMavenInfo._2);
    }

}
