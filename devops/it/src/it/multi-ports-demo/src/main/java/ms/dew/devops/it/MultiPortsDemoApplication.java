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

package ms.dew.devops.it;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import ms.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * MultiPortsDemoApplication.
 *
 * @author 钱奕汎
 */
@SpringCloudApplication
public class MultiPortsDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(MultiPortsDemoApplication.class);

    @Autowired
    private RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(MultiPortsDemoApplication.class, args);
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(DemoVerticle.class, new DeploymentOptions());
        WebClient.create(vertx);

        Dew.Timer.periodic(10, 10, () -> {
            String resp = restTemplate.getForObject("http://multi-ports-demo/hello", String.class);
            logger.info(">>>>> " + resp);
        });
    }

}
