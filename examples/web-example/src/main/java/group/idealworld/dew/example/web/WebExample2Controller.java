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
    @Operation(summary = "示例方法", security = { @SecurityRequirement(name = DewConfig.DEW_AUTH_DOC_FLAG) })
    public Map<String, Integer> test() {
        return new HashMap<>() {
            {
                put("a", 1);
            }
        };
    }

}
