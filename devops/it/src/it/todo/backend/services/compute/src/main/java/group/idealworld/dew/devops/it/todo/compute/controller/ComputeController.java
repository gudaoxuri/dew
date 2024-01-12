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
