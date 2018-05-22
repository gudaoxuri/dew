package com.tairanchina.csp.dew.example.cache;

import com.tairanchina.csp.dew.example.cache.dto.CacheExampleDTO;
import com.tairanchina.csp.dew.example.cache.service.CacheExampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheExampleInitiator implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CacheExampleInitiator.class);

    @Autowired
    private CacheExampleService cacheExampleService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("=======Save=======");
        CacheExampleDTO dto = cacheExampleService.save(new CacheExampleDTO().setId("1").setName("泰然城"));
        assert dto.getId().equals("1");
        // Cache hit!
        dto = cacheExampleService.getById("1");
        assert dto.getName().equals("泰然城");

        logger.info("=======Update=======");
        cacheExampleService.update("1", dto.setName("泰然集团"));
        // Cache hit!
        dto = cacheExampleService.getById("1");
        assert dto.getName().equals("泰然集团");

        logger.info("=======Delete=======");
        cacheExampleService.delete("1");
        dto = cacheExampleService.getById("1");
        assert dto == null;

        logger.info("=======Find=======");
        cacheExampleService.save(new CacheExampleDTO().setId("2").setName("泰然城"));
        cacheExampleService.save(new CacheExampleDTO().setId("3").setName("蜂贷"));
        List<CacheExampleDTO> dtos = cacheExampleService.find();
        assert dtos.size() == 2;
        // Cache hit!
        dtos = cacheExampleService.find();
        assert dtos.size() == 2;
        cacheExampleService.deleteAll();
        dtos = cacheExampleService.find();
        assert dtos.size() == 0;
    }
}
