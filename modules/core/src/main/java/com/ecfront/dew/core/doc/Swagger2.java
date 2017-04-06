package com.ecfront.dew.core.doc;


import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.config.DewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Swagger配置
 *
 * @link https://springfox.github.io/springfox/docs/snapshot/#customizing-the-swagger-endpoints
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Autowired
    private DewConfig dewConfig;

    @Bean
    public Docket createRestApi() {
        if (dewConfig.getBasic().getDoc().getBasePackage().isEmpty()) {
            return null;
        }
        ArrayList<ResponseMessage> responseMessages = new ArrayList<ResponseMessage>() {{
            new ResponseMessageBuilder().code(200).message("成功").build();
            new ResponseMessageBuilder().code(400).message("请求参数错误").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(401).message("权限认证失败").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(403).message("请求资源不可用").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(404).message("请求资源不存在").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(409).message("请求资源冲突").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(415).message("请求格式错误").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(423).message("请求资源被锁定").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(500).message("服务器内部错误").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(501).message("请求方法不存在").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(503).message("服务暂时不可用").responseModel(new ModelRef("Error")).build();
            new ResponseMessageBuilder().code(-1).message("未知异常").responseModel(new ModelRef("Error")).build();
        }};

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(dewConfig.getBasic().getDoc().getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages)
                .globalOperationParameters(new ArrayList<Parameter>() {{
                    new ParameterBuilder()
                            .name(Dew.Constant.TOKEN_VIEW_FLAG)
                            .description("token")
                            .modelRef(new ModelRef("String"))
                            .parameterType("query")
                            .hidden(false)
                            .required(true)
                            .build();
                }});
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
