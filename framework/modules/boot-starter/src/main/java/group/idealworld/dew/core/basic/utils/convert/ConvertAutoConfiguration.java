package group.idealworld.dew.core.basic.utils.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Convert auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
public class ConvertAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Instant convert instant convert.
     *
     * @return the instant convert
     */
    @Bean
    @ConditionalOnMissingBean
    public InstantConvert instantConvert() {
        return new InstantConvert();
    }

    /**
     * Local date converter local date converter.
     *
     * @return the local date converter
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalDateConverter localDateConverter() {
        return new LocalDateConverter();
    }

    /**
     * Local time converter local time converter.
     *
     * @return the local time converter
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalTimeConverter localTimeConverter() {
        return new LocalTimeConverter();
    }

    /**
     * Local date time converter local date time converter.
     *
     * @return the local date time converter
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalDateTimeConverter localDateTimeConverter() {
        return new LocalDateTimeConverter();
    }

}
