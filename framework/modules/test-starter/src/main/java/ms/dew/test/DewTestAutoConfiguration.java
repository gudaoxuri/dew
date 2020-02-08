/*
 * Copyright 2020. the original author or authors.
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

package ms.dew.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Dew test auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
public class DewTestAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DewTestAutoConfiguration.class);

    private RedisServer redisServer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Init.
     *
     * @throws IOException the io exception
     */
    @PostConstruct
    public void init() throws IOException {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        logger.info("Enabled Dew Test");
        redisServer = new RedisServer();
        if (!redisServer.isActive()) {
            try {
                redisServer.start();
                redisTemplate.getConnectionFactory().getConnection();
            } catch (Exception e) {
                logger.warn("Start embedded redis error.");
            }
        }
    }

    /**
     * Destroy.
     */
    @PreDestroy
    public void destroy() {
        if (redisServer.isActive()) {
            redisServer.stop();
        }
    }
}
