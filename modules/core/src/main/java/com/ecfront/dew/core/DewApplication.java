package com.ecfront.dew.core;

import com.ecfront.dew.core.repository.DewRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.persistence.MappedSuperclass;

@SpringCloudApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = DewRepositoryFactoryBean.class)
@EnableTransactionManagement
@MappedSuperclass
@EnableAutoConfiguration(exclude = {FreeMarkerAutoConfiguration.class, GsonAutoConfiguration.class, WebSocketAutoConfiguration.class})
public abstract class DewApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
