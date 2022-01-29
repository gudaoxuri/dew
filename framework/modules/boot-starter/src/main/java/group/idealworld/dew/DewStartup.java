/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.*;
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

    private static final Logger logger = LoggerFactory.getLogger(DewStartup.class);

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
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
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
