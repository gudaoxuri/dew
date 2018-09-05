package com.tairanchina.csp.dew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import java.io.IOException;

@Configuration
public class DewStartup {

    private static final Logger logger = LoggerFactory.getLogger(DewStartup.class);

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    public Filter dewStartupFilter() {
        return new DewStartupFilter();
    }

    @Autowired
    private Dew dew;

    @Order(Integer.MIN_VALUE)
    public class DewStartupFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {

        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {

        }
    }
}
