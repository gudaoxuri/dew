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
                    "spring.datasource.password=" + MYSQL_CONTAINER.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
