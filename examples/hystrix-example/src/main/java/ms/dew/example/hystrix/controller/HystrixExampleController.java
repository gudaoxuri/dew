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

package ms.dew.example.hystrix.controller;

import ms.dew.Dew;
import ms.dew.example.hystrix.service.HystrixExampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
public class HystrixExampleController {

    private static final Logger logger = LoggerFactory.getLogger(HystrixExampleController.class);

    @Autowired
    private HystrixExampleService hystrixExampleService;

    @GetMapping("/exe")
    public String exe() {
        logger.info("Controller Token:" + Dew.context().getToken());
        return hystrixExampleService.getStores(new HashMap<String, Object>() {{
        }}, Dew.context());
    }

}
