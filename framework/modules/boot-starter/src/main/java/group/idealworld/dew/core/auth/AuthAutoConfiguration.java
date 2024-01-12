package group.idealworld.dew.core.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Auth auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
public class AuthAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAutoConfiguration.class);

    /**
     * Basic auth adapter.
     *
     * @return the basic auth adapter
     */
    @Bean
    public BasicAuthAdapter basicAuthAdapter() {
        return new BasicAuthAdapter();
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

}
