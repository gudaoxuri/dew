package group.idealworld.dew.example.skywalking;

import group.idealworld.dew.Dew;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "示例方法", extensions = {
            @Extension(name = "FIN_EXT", properties = @ExtensionProperty(name = "REL", value = "s001,s002")) })
    public String example() {
        return Dew.cluster.trace.getTraceId();
    }

}
