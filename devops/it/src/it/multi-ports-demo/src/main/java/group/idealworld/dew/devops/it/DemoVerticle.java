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

package group.idealworld.dew.devops.it;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DemoVerticle.
 *
 * @author 钱奕汎
 */
public class DemoVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(DemoVerticle.class);

    @Override
    public void start() {
        logger.info("Init Vertx...");
        vertx.createHttpServer(new HttpServerOptions()
                .setTcpKeepAlive(true))
                .requestHandler(new VertxHandler())
                .listen(8010, event -> {
                    if (event.succeeded()) {
                        logger.info("Vertx Http Server Started ");
                    } else {
                        logger.error("Vertx Http Server Start Fail", event.cause());
                    }
                });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
