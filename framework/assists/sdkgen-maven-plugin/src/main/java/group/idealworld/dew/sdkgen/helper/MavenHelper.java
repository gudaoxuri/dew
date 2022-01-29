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

package group.idealworld.dew.sdkgen.helper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Basic mojo.
 *
 * @author gudaoxuri
 */
@Slf4j
public class MavenHelper {

    /**
     * 获取格式化后的Maven属性值.
     *
     * @param standardFlag        标准标识
     * @param formattedProperties Maven属性
     * @return 格式化后的Maven属性值 optional
     */
    public static Optional<String> formatParameters(String standardFlag, Map<String, String> formattedProperties) {
        standardFlag = standardFlag.toLowerCase();
        if (formattedProperties.containsKey(standardFlag)) {
            return Optional.of(formattedProperties.get(standardFlag));
        }
        return Optional.empty();
    }

    /**
     * 获取Maven属性.
     *
     * @param session maven mavenSession
     * @return properties maven properties
     */
    public static Map<String, String> getMavenProperties(MavenSession session) {
        Map<String, String> props = new HashMap<>();
        props.putAll(session.getSystemProperties().entrySet().stream()
                .collect(Collectors.toMap(prop ->
                        prop.getKey().toString().toLowerCase().trim(), prop -> prop.getValue().toString().trim())));
        props.putAll(session.getUserProperties().entrySet().stream()
                .collect(Collectors.toMap(prop ->
                        prop.getKey().toString().toLowerCase().trim(), prop -> prop.getValue().toString().trim())));
        // Support gitlab ci runner by chart.
        props.putAll(props.entrySet().stream()
                .filter(prop -> prop.getKey().startsWith("env."))
                .collect(Collectors.toMap(prop -> prop.getKey().substring("env.".length()), Map.Entry::getValue)));
        return props;
    }

    /**
     * 调用指定的Maven mojo.
     *
     * @param groupId            the group id
     * @param artifactId         the artifact id
     * @param version            the version
     * @param goal               the goal
     * @param configuration      the configuration
     * @param mavenProject       the maven project
     * @param mavenSession       the maven session
     * @param mavenPluginManager the maven plugin manager
     */
    @SneakyThrows
    public static void invoke(String groupId, String artifactId, String version,
                              String goal, Map<String, Object> configuration,
                              MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager mavenPluginManager
    ) {
        log.debug("invoke groupId = " + groupId + " ,artifactId = " + artifactId + " ,version = " + version);
        List<Element> config = configuration.entrySet().stream()
                .map(item -> {
                    if (item.getValue() instanceof Map<?, ?>) {
                        var eles = ((Map<?, ?>) item.getValue()).entrySet().stream()
                                .map(subItem -> element(subItem.getKey().toString(), subItem.getValue().toString()))
                                .collect(Collectors.toList());
                        return element(item.getKey(), eles.toArray(new MojoExecutor.Element[eles.size()]));
                    } else {
                        return element(item.getKey(), item.getValue().toString());
                    }
                })
                .collect(Collectors.toList());
        org.apache.maven.model.Plugin plugin;
        if (version == null) {
            plugin = plugin(groupId, artifactId);
        } else {
            plugin = plugin(groupId, artifactId, version);
        }
        executeMojo(
                plugin,
                goal(goal),
                configuration(config.toArray(new Element[]{})),
                executionEnvironment(
                        mavenProject,
                        mavenSession,
                        mavenPluginManager
                )
        );
    }

}
