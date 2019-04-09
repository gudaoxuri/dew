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

package ms.dew.devops.it.todo.kernel.controller;

import com.ecfront.dew.common.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ms.dew.devops.it.todo.kernel.domain.Todo;
import ms.dew.devops.it.todo.kernel.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * To-do controller.
 *
 * @author gudaoxuri
 */
@RestController
@Api("TODO示例")
@RequestMapping("/api")
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * Find all page.
     *
     * @param pageNumber page number，从1开始
     * @param pageSize   page size
     * @return the page
     */
    @GetMapping("")
    @ApiOperation(value = "获取Todo列表")
    public Page<Todo> findAll(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return todoService.list(pageNumber, pageSize);
    }

    /**
     * Add int.
     *
     * @param content the content
     * @return the int
     */
    @PostMapping("")
    @ApiOperation(value = "添加Todo记录")
    public Todo add(@RequestBody String content) {
        return todoService.add(content);
    }

    /**
     * Delete.
     *
     * @param id the id
     */
    @DeleteMapping("{id}")
    @ApiOperation(value = "删除Todo记录")
    public boolean delete(@PathVariable("id") int id) {
        return todoService.delete(id);
    }

    /**
     * Sort.
     *
     * @param id      the id
     * @param afterBy the after by
     */
    @PutMapping("{id}/sort")
    @ApiOperation(value = "调整Todo记录顺序")
    public boolean sort(@PathVariable("id") int id,
                     @RequestParam(value = "afterBy") int afterBy) {
        return todoService.sort(id, afterBy);
    }

}
