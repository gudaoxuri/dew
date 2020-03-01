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

package group.idealworld.dew.example.tracing;


import group.idealworld.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Tracing 2 example application.
 *
 * @author gudaoxuri
 */
@SpringCloudApplication
@Configuration
public class Tracing2ExampleApplication {

    private Logger logger = LoggerFactory.getLogger(Tracing2ExampleApplication.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(Tracing2ExampleApplication.class).run(args);
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        logger.info("开始监听..");
        Dew.cluster.mq.subscribe("test", message -> logger.info("pub_sub->{}", message));
    }
}
