package group.idealworld.dew.devops.it.todo.kernel.controller;

import com.ecfront.dew.common.Page;
import group.idealworld.dew.devops.it.todo.kernel.domain.Todo;
import group.idealworld.dew.devops.it.todo.kernel.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * To-do controller.
 *
 * @author gudaoxuri
 */
@RestController
// Swagger文档注解
@Tag(name = "TODO示例")
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
    @Operation(summary = "获取Todo列表")
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
    @Operation(summary = "添加Todo记录")
    public Todo add(@RequestBody String content) {
        return todoService.add(content);
    }

    /**
     * Delete.
     *
     * @param id the id
     * @return boolean
     */
    @DeleteMapping("{id}")
    @Operation(summary = "删除Todo记录")
    public boolean delete(@PathVariable("id") int id) {
        return todoService.delete(id);
    }

    /**
     * Sort.
     *
     * @param id      the id
     * @param afterBy the after by
     * @return boolean
     */
    @PutMapping("{id}/sort")
    @Operation(summary = "调整Todo记录顺序")
    public boolean sort(@PathVariable("id") int id, @RequestParam(value = "afterBy") int afterBy) {
        return todoService.sort(id, afterBy);
    }

}
