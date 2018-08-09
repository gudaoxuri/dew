package com.tairanchina.csp.dew.core.doc;


import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.core.DewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Swagger配置
 *
 * @link https://springfox.github.io/springfox/docs/snapshot/#customizing-the-swagger-endpoints
 */
@Configuration
@ConditionalOnProperty(prefix = "dew.basic.doc", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(EnableSwagger2.class)
@EnableSwagger2
public class DocProcessor implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DocProcessor.class);

    // FIXME 此设置可能无效
    @Value("${server.ssl.key-store:}")
    private String sslKeyStore;

    @Autowired
    private DewConfig dewConfig;

    @Value("${server.context-path:}")
    private String contextPath;

    @Value("${api.file.name:index}")
    private String apiFileName;

    @Bean
    public Docket restApi(ServletContext servletContext) {
        if (dewConfig.getBasic().getDoc().getBasePackage().isEmpty()) {
            return null;
        }
        return new Docket(DocumentationType.SWAGGER_2)
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

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        int port = event.getEmbeddedServletContainer().getPort();
        String generate = System.getProperty("dew.doc.generate");
        if (generate != null && generate.equalsIgnoreCase("true")) {
            String outputDir = System.getProperty("dew.doc.outputDir");
            String swaggerDir = System.getProperty("dew.doc.swaggerDir");
            logger.info("Generating Doc to :" + outputDir);
            try {
                Files.createDirectories(Paths.get(outputDir));
                if (!new File(outputDir + File.separator + "asciidoc").exists()) {
                    // Create index.adoc
                    Files.createDirectories(Paths.get(outputDir + File.separator + "asciidoc"));
                    try (BufferedWriter writer =
                                 Files.newBufferedWriter(
                                         Paths.get(outputDir + File.separator + "asciidoc", apiFileName + ".adoc"),
                                         StandardCharsets.UTF_8)) {
                        writer.write("include::{generated}/overview.adoc[]\n" +
                                "include::{generated}/paths.adoc[]\n" +
                                "include::{generated}/security.adoc[]\n" +
                                "include::{generated}/definitions.adoc[]");
                    }
                }
                // Create swagger.json
                String swaggerJson = $.http.get((sslKeyStore == null || sslKeyStore.isEmpty() ? "http" : "https") + "://localhost:" + port + contextPath + "/v2/api-docs");
                Files.createDirectories(Paths.get(swaggerDir));
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(swaggerDir, "swagger.json"), StandardCharsets.UTF_8)) {
                    writer.write(swaggerJson);
                }
            } catch (IOException e) {
                logger.error("Has error", e);
            }
        }
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(dewConfig.getBasic().getName())
                .description(dewConfig.getBasic().getDesc())
                .termsOfServiceUrl(dewConfig.getBasic().getWebSite())
                .version(dewConfig.getBasic().getVersion())
                .build();
    }

}
