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

package ms.dew.core.doc;


import ms.dew.Dew;
import ms.dew.core.DewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

/**
 * Swagger配置
 *
 * @link https://springfox.github.io/springfox/docs/snapshot/#customizing-the-swagger-endpoints
 */
@Configuration
@ConditionalOnProperty(prefix = "dew.basic.doc", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableSwagger2
@ImportAutoConfiguration({DocClusterAutoConfiguration.class, DocLocalAutoConfiguration.class})
public class DocAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DocAutoConfiguration.class);

    public static final String FLAG_APPLICATION_NAME = "applicationName";

    @Autowired
    private DewConfig dewConfig;

    @Value("${server.context-path:}")
    private String contextPath;

    @Bean
    public DocService docService() {
        return new DocService();
    }

    @Bean
    public Docket restApi(ServletContext servletContext) {
        return new Docket(DocumentationType.SWAGGER_2)
                .tags(new Tag(FLAG_APPLICATION_NAME, Dew.Info.name))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(dewConfig.getBasic().getDoc().getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                /*.securitySchemes(new ArrayList<ApiKey>() {{
                    add(new ApiKey("access_token", "accessToken", "develop"));
                }})
                .globalOperationParameters(new ArrayList<Parameter>() {{
                    add(new ParameterBuilder()
                            .name(Dew.dewConfig.getSecurity().getTokenFlag())
                            .description("token 用于鉴权，部分接口必须传入")
                            .modelRef(new ModelRef("String"))
                            .parameterType(Dew.dewConfig.getSecurity().isTokenInHeader() ? "header" : "query")
                            .hidden(false)
                            .required(true)
                            .build());
                }})*/
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return contextPath + super.getApplicationBasePath();
                    }
                });
    }

    private ApiInfo apiInfo() {
        ApiInfoBuilder builder = new ApiInfoBuilder()
                .title(dewConfig.getBasic().getName())
                .description(dewConfig.getBasic().getDesc())
                .termsOfServiceUrl(dewConfig.getBasic().getWebSite())
                .version(dewConfig.getBasic().getVersion());
        if (dewConfig.getBasic().getDoc().getContact() != null) {
            builder.contact(new Contact(
                    dewConfig.getBasic().getDoc().getContact().getName(),
                    dewConfig.getBasic().getDoc().getContact().getUrl(),
                    dewConfig.getBasic().getDoc().getContact().getEmail()
            ));
        }
        return builder.build();
    }

}
