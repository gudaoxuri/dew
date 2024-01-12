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
