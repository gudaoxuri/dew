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

package group.idealworld.dew.core.doc;


import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Swagger配置.
 *
 * @author gudaoxuri
 * @see <a href="https://springdoc.org/migrating-from-springfox.html">migrating-from-springfox</a>
 */
@Configuration
@ConditionalOnProperty(prefix = "dew.basic.doc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DocAutoConfiguration {

    private static final List<Function<PathItem, Operation>> OPERATION_GETTERS = Arrays.asList(
            PathItem::getGet, PathItem::getPost, PathItem::getDelete, PathItem::getHead,
            PathItem::getOptions, PathItem::getPatch, PathItem::getPut);

    /**
     * Dew default group grouped open api.
     *
     * @return the grouped open api
     */
    @Bean
    public GroupedOpenApi dewDefaultGroup() {
        return GroupedOpenApi.builder()
                .group("dew-default")
                .packagesToScan(Dew.dewConfig.getBasic().getDoc().getBasePackage())
                .build();
    }

    /**
     * Dew default api open api.
     *
     * @return the open api
     */
    @Bean
    public OpenAPI dewDefaultAPI() {
        var openAPI = new OpenAPI()
                .info(new Info().title(Dew.dewConfig.getBasic().getName())
                        .description(Dew.dewConfig.getBasic().getDesc())
                        .version(Dew.dewConfig.getBasic().getVersion())
                        .termsOfService(Dew.dewConfig.getBasic().getWebSite()));
        if (Dew.dewConfig.getBasic().getDoc().getContact() != null) {
            openAPI.getInfo().contact(new Contact()
                    .name(Dew.dewConfig.getBasic().getDoc().getContact().getName())
                    .email(Dew.dewConfig.getBasic().getDoc().getContact().getEmail())
                    .url(Dew.dewConfig.getBasic().getDoc().getContact().getUrl())
            );
        }
        // Add Auth
        openAPI.components(new Components()
                .addSecuritySchemes(DewConfig.DEW_AUTH_DOC_FLAG, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .name(Dew.dewConfig.getSecurity().getTokenFlag())
                        .in(Dew.dewConfig.getSecurity().isTokenInHeader()
                                ? SecurityScheme.In.HEADER : SecurityScheme.In.QUERY))
        );
        return openAPI;
    }

    // TODO Add dynamic auth
    /*public void addAuthAPI(OpenAPI openAPI) {
        openAPI.getPaths().forEach((path, item) ->
                getOperations(item).forEach(operation -> {
                    operation.setSecurity(new ArrayList<>() {
                        {
                            add(new SecurityRequirement().addList(DewConfig.DEW_AUTH_DOC_FLAG));
                        }
                    });
                }));
    }*/

    private static Stream<Operation> getOperations(PathItem pathItem) {
        return OPERATION_GETTERS.stream()
                .map(getter -> getter.apply(pathItem))
                .filter(Objects::nonNull);
    }

}
