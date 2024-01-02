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

package group.idealworld.dew.devops.it.todo.compute.controller;

import group.idealworld.dew.devops.it.todo.compute.service.ComputeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;

/**
 * Compute controller.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "TODO计算")
@RequestMapping("/")
public class ComputeController {

    @Autowired
    private ComputeService computeService;


    /**
     * Compute string.
     *
     * @param jsCode the js code
     * @return result
     * @throws ScriptException the script exception
     */
    @PutMapping("compute")
    @Operation(summary = "执行计算")
    public String compute(@RequestBody String jsCode) throws ScriptException {
        return computeService.compute(jsCode);
    }

}
