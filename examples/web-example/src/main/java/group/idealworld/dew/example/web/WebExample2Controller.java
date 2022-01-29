/*
 * Copyright 2022. the original author or authors
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

import group.idealworld.dew.core.DewConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RestController
@Tag(name = "exmaple2", description = "示例应用2说明")
@Validated // URL 类型的验证需要使用此注解
public class WebExample2Controller {

    /**
     * 最基础的Controller示例.
     *
     * @return result
     */
    @GetMapping("test")
    @Operation(summary = "示例方法",
            security = { @SecurityRequirement(name = DewConfig.DEW_AUTH_DOC_FLAG) })
    public Map<String, Integer> test() {
        return new HashMap<>() {
            {
                put("a", 1);
            }
        };
    }

}
