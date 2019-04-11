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

package ms.dew.devops;

import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Basic test.
 *
 * @author gudaoxuri
 */
public abstract class BasicTest {

    /**
     * The constant defaultKubeConfig.
     */
    protected static String defaultKubeConfig;
    /**
     * The constant defaultDockerHost.
     */
    protected static String defaultDockerHost;
    /**
     * The constant defaultDockerRegistryUrl.
     */
    protected static String defaultDockerRegistryUrl;
    /**
     * The constant defaultDockerRegistryUserName.
     */
    protected static String defaultDockerRegistryUserName;
    /**
     * The constant defaultDockerRegistryPassword.
     */
    protected static String defaultDockerRegistryPassword;

    /**
     * Init test.
     *
     * @throws IOException the io exception
     */
    @BeforeClass
    public static void initTest() throws IOException {
        Properties properties = new Properties();
        properties.load(
                new FileInputStream(
                        Paths.get("").toFile().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + File.separator
                                + "devops-test.properties"));
        defaultKubeConfig = properties.getProperty("dew.devops.kube.config");
        defaultDockerHost = properties.getProperty("dew.devops.docker.host");
        defaultDockerRegistryUrl = properties.getProperty("dew.devops.docker.registry.url");
        defaultDockerRegistryUserName = properties.getProperty("dew.devops.docker.registry.username");
        defaultDockerRegistryPassword = properties.getProperty("dew.devops.docker.registry.password");
    }
}
