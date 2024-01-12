package group.idealworld.dew.core.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Servlet;

/**
 * Error auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dew.basic.format", name = "use-unity-error", havingValue = "true", matchIfMissing = true)
public class ErrorAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Error controller error controller.
     *
     * @param errorAttributes the error attributes
     * @return the error controller
     */
    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public ErrorController errorController(ErrorAttributes errorAttributes) {
        return new ErrorController(errorAttributes);
    }
}
