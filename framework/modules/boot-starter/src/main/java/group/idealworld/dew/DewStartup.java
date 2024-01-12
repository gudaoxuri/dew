package group.idealworld.dew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import java.io.IOException;

/**
 * Dew startup.
 * <p>
 * 此类用于确保Dew对象最先被加载
 *
 * @author gudaoxuri
 */
@Configuration
public class DewStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(DewStartup.class);

    @Autowired
    private Dew dew;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Dew startup filter filter.
     *
     * @return the filter
     */
    @Bean
    public Filter dewStartupFilter() {
        return new DewStartupFilter();
    }

    /**
     * Dew startup filter.
     */
    @Order(Integer.MIN_VALUE)
    public class DewStartupFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // Do nothing.
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {
            // Do nothing.
        }
    }
}
