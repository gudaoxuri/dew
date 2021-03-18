/*
 * Copyright 2021. the original author or authors.
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

import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

/**
 * 测试基础类.
 *
 * @author gudaoxuri
 */
@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class DewTest {

    @ClassRule
    protected static GenericContainer redisConfig = new GenericContainer("redis:6-alpine")
            .withExposedPorts(6379);

    @ClassRule
    protected static JdbcDatabaseContainer mysqlConfig = new MySQLContainer("mysql:8");

    @ClassRule
    protected static RabbitMQContainer rabbitMQConfig = new RabbitMQContainer("management-alpine");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mysqlConfig.getJdbcUrl(),
                    "spring.datasource.username=" + mysqlConfig.getUsername(),
                    "spring.datasource.password=" + mysqlConfig.getPassword(),
                    "spring.redis.host=" + redisConfig.getHost(),
                    "spring.redis.port=" + redisConfig.getFirstMappedPort(),
                    "spring.redis.password=",
                    "spring.rabbitmq.host=" + rabbitMQConfig.getHost(),
                    "spring.rabbitmq.port=" + rabbitMQConfig.getHttpPort()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    protected static void enableRedis() {
        redisConfig.start();
        System.out.println("Test Redis port: " + redisConfig.getFirstMappedPort());
    }

    protected static void enableMysql() {
        var scriptPath = ClassLoader.getSystemResource("").getPath() + "/sql/init.sql";
        if (new File(scriptPath).exists()) {
            mysqlConfig.withInitScript("sql/init.sql");
        }
        mysqlConfig.withCommand("--max_allowed_packet=10M");
        mysqlConfig.start();
        System.out.println("Test mysql port: " + mysqlConfig.getFirstMappedPort()
                + ", username: " + mysqlConfig.getUsername() + ", password: " + mysqlConfig.getPassword());
    }

    protected static void enableRabbit() {
        rabbitMQConfig.start();
        System.out.println("Test Rabbit port: " + rabbitMQConfig.getHttpPort());
    }
}
