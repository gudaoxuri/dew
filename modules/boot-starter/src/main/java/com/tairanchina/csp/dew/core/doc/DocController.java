package com.tairanchina.csp.dew.core.doc;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.function.Supplier;

@RestController
@ApiIgnore
@Validated
@ConditionalOnWebApplication
@RequestMapping("${management.context-path:/management-admin}/doc")
public class DocController {

    private static final Logger logger = LoggerFactory.getLogger(DocController.class);

    private Supplier<List<String>> fetchSwaggerJsonUrlsFun;

    @Autowired
    private DocService docService;

    public DocController(Supplier<List<String>> fetchSwaggerJsonUrlsFun) {
        this.fetchSwaggerJsonUrlsFun = fetchSwaggerJsonUrlsFun;
    }

    @PutMapping("offline")
    public String generateOfflineDoc(@Validated @RequestBody OfflineDocGenerateReq request) throws Exception {
        List<String> swaggerJsonUrls =
                (request.getSwaggerJsonUrls() == null || request.getSwaggerJsonUrls().isEmpty()) ?
                        fetchSwaggerJsonUrlsFun.get() : request.getSwaggerJsonUrls();
        Resp<String> result = docService.generateOfflineDoc(request.getDocName(), request.getDocDesc(), request.getVisitUrls(), swaggerJsonUrls);
        if (result.ok()) {
            return result.getBody();
        } else {
            throw Dew.E.e(result.getCode(), new Exception(result.getMessage()));
        }
    }


}
