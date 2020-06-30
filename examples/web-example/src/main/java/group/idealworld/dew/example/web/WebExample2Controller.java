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

package group.idealworld.dew.example.web;

import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Web example2 controller.
 *
 * @author gudaoxuri
 */
@RestController("example2")
@Api("示例应用2")
@Validated // URL 类型的验证需要使用此注解
@SwaggerDefinition(
        info = @Info(
                title = "the title",
                version = "0.0",
                description = "My API",
                license = @License(name = "Apache 2.0", url = "http://foo.bar"),
                contact = @Contact(url = "http://gigantic-server.com", name = "Fred", email = "Fred@gigagantic-server.com")
        ),
        tags = {
                @Tag(name = "Tag 1", description = "desc 1"),
                @Tag(name = "Tag 2", description = "desc 2"),
                @Tag(name = "Tag 3")
        },
        basePath = "http://127.0.0.1:809"
)
public class WebExample2Controller {

    /**
     * 最基础的Controller示例.
     *
     * @return result
     */
    @GetMapping("test")
    @ApiOperation(value = "示例方法")
    public Map<String, Integer> test() {
        return new HashMap<>() {
            {
                put("a", 1);
            }
        };
    }

}
