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

package ms.dew.example.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller.
 *
 * @author gudaoxuri
 */
@RestController
@Api(description = "示例应用")
@RequestMapping("/")
public class ExampleController {

    /**
     * Example 100.
     *
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    @GetMapping("/example/100")
    @ApiOperation(value = "示例，延时100")
    public String example100() throws InterruptedException {
        Thread.sleep(100);
        return "ok";
    }

    /**
     * Example 1000.
     *
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    @GetMapping("/example/1000")
    @ApiOperation(value = "示例，延时1000")
    public String example1000() throws InterruptedException {
        Thread.sleep(1000);
        return "ok";
    }

    /**
     * Example 10000.
     *
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    @GetMapping("/example/10000")
    @ApiOperation(value = "示例，延时10000")
    public String example10000() throws InterruptedException {
        Thread.sleep(10000);
        return "ok";
    }

}
