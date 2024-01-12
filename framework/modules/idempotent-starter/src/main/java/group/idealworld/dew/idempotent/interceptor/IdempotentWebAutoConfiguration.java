package group.idealworld.dew.idempotent.interceptor;

import group.idealworld.dew.idempotent.DewIdempotentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Idempotent web mvc configurer.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(DewIdempotentConfig.class)
@Order(10000)
public class IdempotentWebAutoConfiguration implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdempotentWebAutoConfiguration.class);

    private DewIdempotentConfig dewIdempotentConfig;

    /**
     * Instantiates a new Idempotent web mvc configurer.
     *
     * @param dewIdempotentConfig the dew idempotent config
     */
    public IdempotentWebAutoConfiguration(DewIdempotentConfig dewIdempotentConfig) {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
        this.dewIdempotentConfig = dewIdempotentConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IdempotentHandlerInterceptor(dewIdempotentConfig)).excludePathPatterns("/error/**");
    }

}
