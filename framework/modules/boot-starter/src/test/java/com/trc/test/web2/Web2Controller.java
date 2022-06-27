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

package com.trc.test.web2;

import com.ecfront.dew.common.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web controller.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "test2", description = "Test2 API")
@RequestMapping(value = "/test2/")
@Validated
public class Web2Controller {

    @GetMapping(value = "test2")
    @Operation(summary = "test2")
    public Resp<String> test2(@Parameter(name = "q", in = ParameterIn.QUERY, required = true) @RequestParam String q) {
        return Resp.success("test2");
    }


}
