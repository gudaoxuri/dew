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

package group.idealworld.dew.example.skywalking;

import group.idealworld.dew.Dew;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Web example controller.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "example", description = "示例应用说明")
@Validated // URL 类型的验证需要使用此注解
public class SkyWalkingExampleController {

    /**
     * 最基础的Controller示例.
     *
     * @return result
     */
    @GetMapping("example")
    @Operation(summary = "示例方法", extensions = {@Extension(name = "FIN_EXT", properties = @ExtensionProperty(name = "REL", value = "s001,s002"))})
    public String example() {
        return Dew.cluster.trace.getTraceId();
    }

}
