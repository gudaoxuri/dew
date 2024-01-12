package group.idealworld.dew.core.web.interceptor;

import group.idealworld.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

/**
 * Interceptor web auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnWebApplication
@Order(20000)
public class InterceptorWebAutoConfiguration implements WebMvcConfigurer {

    @Value(("${springdoc.api-docs.path:/v3/api-docs}"))
    private String openApiDocPath;

    @Value(("${springdoc.swagger-ui.path:/swagger-ui}"))
    private String openApiUiPath;

    @Value(("${management.endpoints.web.base-path:/management}"))
    private String managementPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorWebAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (Dew.dewConfig == null) {
            // 未启用web的情况下，Dew加载滞后，忽略
            return;
        }
        if (!Dew.dewConfig.getSecurity().isAuthEnabled()) {
            return;
        }
        if (Dew.dewConfig.getSecurity().isIdentInfoEnabled()) {
            registry.addInterceptor(new IdentInfoHandlerInterceptor())
                    .excludePathPatterns("/error/**")
                    .excludePathPatterns(openApiUiPath + "/**")
                    .excludePathPatterns(openApiDocPath + "/**")
                    .excludePathPatterns(managementPath + "/**");
        } else {
            registry.addInterceptor(new TokenHandlerInterceptor())
                    .excludePathPatterns("/error/**")
                    .excludePathPatterns(openApiUiPath + "/**")
                    .excludePathPatterns(openApiDocPath + "/**")
                    .excludePathPatterns(managementPath + "/**");
        }
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()) {
            registry.addInterceptor(new RouterHandlerInterceptor())
                    .excludePathPatterns("/error/**")
                    .excludePathPatterns(openApiUiPath + "/**")
                    .excludePathPatterns(openApiDocPath + "/**")
                    .excludePathPatterns(managementPath + "/**");
        }
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
