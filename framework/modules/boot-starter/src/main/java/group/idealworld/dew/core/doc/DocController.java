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

package group.idealworld.dew.core.doc;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
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

/**
 * Doc controller.
 *
 * @author gudaoxuri
 */
@RestController
@ApiIgnore
@Validated
@ConditionalOnWebApplication
@RequestMapping("${management.endpoints.web.base-path:/actuator}/doc")
public class DocController {

    private Supplier<List<String>> fetchSwaggerJsonUrlsFun;

    @Autowired
    private DocService docService;

    /**
     * Instantiates a new Doc controller.
     *
     * @param fetchSwaggerJsonUrlsFun the fetch swagger json urls fun
     */
    public DocController(Supplier<List<String>> fetchSwaggerJsonUrlsFun) {
        this.fetchSwaggerJsonUrlsFun = fetchSwaggerJsonUrlsFun;
    }

    /**
     * Generate offline doc to string.
     *
     * @param request the request
     * @return the string
     * @throws Exception the exception
     */
    @PutMapping("offline")
    public String generateOfflineDoc(@Validated @RequestBody OfflineDocGenerateReq request) throws Exception {
        List<String> swaggerJsonUrls =
                (request.getSwaggerJsonUrls() == null
                        || request.getSwaggerJsonUrls().isEmpty()) ? fetchSwaggerJsonUrlsFun.get() : request.getSwaggerJsonUrls();
        Resp<String> result = docService.generateOfflineDoc(request.getDocName(), request.getDocDesc(), request.getVisitUrls(), swaggerJsonUrls);
        if (result.ok()) {
            return result.getBody();
        } else {
            throw Dew.E.e(result.getCode(), new Exception(result.getMessage()));
        }
    }


}
