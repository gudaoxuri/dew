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

package ms.dew.devops.kernel.config;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Final config.
 * <p>
 * 最终生成的配置
 *
 * @author gudaoxuri
 */
public class FinalConfig {

    private Map<String, FinalProjectConfig> projects = new LinkedHashMap<>();

    private MavenSession mavenSession;
    private BuildPluginManager pluginManager;

    /**
     * Gets projects.
     *
     * @return the projects
     */
    public Map<String, FinalProjectConfig> getProjects() {
        return projects;
    }

    /**
     * Gets maven session.
     *
     * @return the maven session
     */
    public MavenSession getMavenSession() {
        return mavenSession;
    }

    /**
     * Sets maven session.
     *
     * @param mavenSession the maven session
     */
    public void setMavenSession(MavenSession mavenSession) {
        this.mavenSession = mavenSession;
    }

    /**
     * Gets plugin manager.
     *
     * @return the plugin manager
     */
    public BuildPluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Sets plugin manager.
     *
     * @param pluginManager the plugin manager
     */
    public void setPluginManager(BuildPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}
