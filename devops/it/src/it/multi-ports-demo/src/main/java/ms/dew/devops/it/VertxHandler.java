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

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.stereotype.Component;

/**
 * VertxHandler.
 *
 * @author 钱奕汎
 */
@Component
public class VertxHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {

        if (request.method().equals(HttpMethod.GET) && request.path().equals("/hello")) {
            request.bodyHandler(buffer ->
                    request.response().setStatusCode(200).end("vertx"));
        } else {
            request.response().setStatusCode(404).end("Not Found!");
        }
    }
}
