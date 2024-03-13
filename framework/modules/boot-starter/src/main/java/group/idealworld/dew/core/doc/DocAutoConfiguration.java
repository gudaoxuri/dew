package group.idealworld.dew.core.doc;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * Swagger配置.
 *
 * @author gudaoxuri
 * @see <a href=
 * "https://springdoc.org/migrating-from-springfox.html">migrating-from-springfox</a>
 */
@Configuration
@ConditionalOnProperty(prefix = "dew.basic.doc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DocAutoConfiguration {

    @Bean
    public GroupedOpenApi dewDefaultGroup() {
        return GroupedOpenApi.builder()
                .group("dew-default")
                .addOpenApiCustomizer(openApi -> openApi.getPaths().values().stream()
                        .flatMap(pathItem -> pathItem.readOperations().stream())
                        .forEach(operation -> Dew.dewConfig.getBasic().getDoc()
                                .getRequestHeaders()
                                .forEach((key, value) -> operation.addParametersItem(
                                        new HeaderParameter().$ref(
                                                "#/components/parameters/"
                                                        + key)))))
                .packagesToScan(Dew.dewConfig.getBasic().getDoc().getBasePackage().split(";"))
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        var openAPI = new OpenAPI()
                .info(new Info().title(Dew.dewConfig.getBasic().getName())
                        .description(Dew.dewConfig.getBasic().getDesc())
                        .version(Dew.dewConfig.getBasic().getVersion())
                        .termsOfService(Dew.dewConfig.getBasic().getWebSite()));
        if (!Dew.dewConfig.getBasic().getDoc().getServers().isEmpty()) {
            openAPI.servers(
                    Dew.dewConfig.getBasic().getDoc().getServers().entrySet().stream()
                            .map(entry -> new Server().url(entry.getValue())
                                    .description(entry.getKey()))
                            .collect(Collectors.toList()));
        }
        if (Dew.dewConfig.getBasic().getDoc().getContact() != null) {
            openAPI.getInfo().contact(new Contact()
                    .name(Dew.dewConfig.getBasic().getDoc().getContact().getName())
                    .email(Dew.dewConfig.getBasic().getDoc().getContact().getEmail())
                    .url(Dew.dewConfig.getBasic().getDoc().getContact().getUrl()));
        }
        // Add Auth
        var components = new Components()
                .addSecuritySchemes(DewConfig.DEW_AUTH_DOC_FLAG, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .name(Dew.dewConfig.getSecurity().getTokenFlag())
                        .in(Dew.dewConfig.getSecurity().isTokenInHeader()
                                ? SecurityScheme.In.HEADER
                                : SecurityScheme.In.QUERY));
        if (!Dew.dewConfig.getBasic().getDoc().getRequestHeaders().isEmpty()) {
            Dew.dewConfig.getBasic().getDoc().getRequestHeaders().forEach((key, value) -> components
                    .addParameters(key, new HeaderParameter().required(false).name(key)
                            .description(value).schema(new StringSchema())));
        }
        openAPI.components(components);
        return openAPI;
    }

}
