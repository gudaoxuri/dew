/*
 * Copyright 2023. the original author or authors
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

public class PostgreSqlExtension implements BeforeAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(PostgreSqlExtension.class);

    private static final JdbcDatabaseContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(DockerImageName.parse("postgres:12-alpine"));

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        var scriptPath = ClassLoader.getSystemResource("").getPath() + "/sql/pg_init.sql";
        if (new File(scriptPath).exists()) {
            POSTGRESQL_CONTAINER.withInitScript("sql/pg_init.sql");
        }
        POSTGRESQL_CONTAINER.start();
        logger.info("Test postgresql port: " + POSTGRESQL_CONTAINER.getExposedPorts()
                + ", username: " + POSTGRESQL_CONTAINER.getUsername() + ", password: " + POSTGRESQL_CONTAINER.getPassword());
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.datasource.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "spring.data.datasource.username=" + POSTGRESQL_CONTAINER.getUsername(),
                    "spring.data.datasource.password=" + POSTGRESQL_CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
