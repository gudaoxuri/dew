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

package group.idealworld.dew.devops.it;

import io.kubernetes.client.openapi.ApiException;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Basic processor.
 *
 * @author gudaoxuri
 */
public abstract class BasicProcessor {

    /**
     * The constant logger.
     */
    protected static final Logger logger = LoggerFactory.getLogger(BasicProcessor.class);

    /**
     * The Kube config.
     */
    protected String kubeConfig;
    /**
     * The Docker host.
     */
    protected String dockerHost;
    /**
     * The Docker registry url.
     */
    protected String dockerRegistryUrl;
    /**
     * The Docker registry user name.
     */
    protected String dockerRegistryUserName;
    /**
     * The Docker registry password.
     */
    protected String dockerRegistryPassword;
    /**
     * The It snapshot repository id.
     */
    protected String itSnapshotRepositoryId;
    /**
     * The It snapshot repository url.
     */
    protected String itSnapshotRepositoryUrl;

    /**
     * Load config.
     *
     * @throws IOException  the io exception
     * @throws ApiException the api exception
     */
    @Before
    public void loadConfig() throws IOException {
        String configPath = Paths.get("").toFile().getAbsoluteFile().getParentFile().getAbsolutePath() + File.separator + "devops-test.properties";
        logger.info("Load config from " + configPath);
        Properties properties = new Properties();
        properties.load(new FileInputStream(configPath));
        kubeConfig = properties.getProperty("dew_devops_kube_config");
        dockerHost = properties.getProperty("dew_devops_docker_host");
        dockerRegistryUrl = properties.getProperty("dew_devops_docker_registry_url");
        dockerRegistryUserName = properties.getProperty("dew_devops_docker_registry_username");
        dockerRegistryPassword = properties.getProperty("dew_devops_docker_registry_password");
        itSnapshotRepositoryId = properties.getProperty("dew_devops_it_snapshotRepository_id");
        itSnapshotRepositoryUrl = properties.getProperty("dew_devops_it_snapshotRepository_url");
    }
}
