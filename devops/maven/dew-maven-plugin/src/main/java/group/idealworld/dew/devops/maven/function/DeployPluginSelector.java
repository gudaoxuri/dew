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

package group.idealworld.dew.devops.maven.function;

import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_native.FrontendNativeNodeAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_non_natvie.FrontendNonNativeNodeAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.jvmservice_springboot.JvmServiceSpringBootAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.deploy.DeployPlugin;
import group.idealworld.dew.devops.kernel.plugin.deploy.kubernetes.KubernetesDeployPlugin;
import group.idealworld.dew.devops.kernel.plugin.deploy.maven.MavenDeployPlugin;
import org.apache.maven.project.MavenProject;


/**
 * 部署插件选择器.
 *
 * @author gudaoxuri
 */
public class DeployPluginSelector {

    /**
     * Select deploy plugin.
     *
     * @param appKindPlugin the app kind plugin
     * @param mavenProject  the maven project
     * @return the deploy plugin
     */
    public static DeployPlugin select(AppKindPlugin appKindPlugin, MavenProject mavenProject) {
        if (appKindPlugin instanceof FrontendNonNativeNodeAppKindPlugin
                || appKindPlugin instanceof FrontendNativeNodeAppKindPlugin
                || appKindPlugin instanceof JvmServiceSpringBootAppKindPlugin) {
            return new KubernetesDeployPlugin();
        }
        return new MavenDeployPlugin();
    }

}
