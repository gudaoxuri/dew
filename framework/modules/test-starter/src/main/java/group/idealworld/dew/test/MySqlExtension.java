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

package group.idealworld.dew.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

public class MySqlExtension implements BeforeAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlExtension.class);



    private static final JdbcDatabaseContainer MYSQL_CONTAINER = new MySQLContainer(DockerImageName.parse("mysql:8"));

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        var scriptPath = ClassLoader.getSystemResource("").getPath() + "/sql/init.sql";
        if (new File(scriptPath).exists()) {
            MYSQL_CONTAINER.withInitScript("sql/init.sql");
        }
        MYSQL_CONTAINER.withCommand("--max_allowed_packet=10M");
        MYSQL_CONTAINER.start();
        LOGGER.info("Test mysql port: " + MYSQL_CONTAINER.getFirstMappedPort()
                + ", username: " + MYSQL_CONTAINER.getUsername() + ", password: " + MYSQL_CONTAINER.getPassword());
    }

    /**
     * Initializer.
     */
    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        /**
         * Initialize.
         *
         * @param configurableApplicationContext the configurable application context
         */
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + MYSQL_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + MYSQL_CONTAINER.getUsername(),
                    "spring.datasource.password=" + MYSQL_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
