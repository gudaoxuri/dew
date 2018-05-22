package com.tairanchina.csp.dew.example.cache.service;

import com.tairanchina.csp.dew.example.cache.dto.CacheExampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "csp:example:cache")
/**
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html
 */
public class CacheExampleService {

    private static final Logger logger = LoggerFactory.getLogger(CacheExampleService.class);

    private static final Map<String, CacheExampleDTO> CONTAINER = new HashMap<>();

    @CachePut(key = "#dto.id")
    public CacheExampleDTO save(CacheExampleDTO dto) {
        CONTAINER.put(dto.getId(), dto);
        return dto;
    }

    @CachePut(key = "#id")
    public CacheExampleDTO update(String id, CacheExampleDTO dto) {
        // 同一类方法调用使用此方式才能被AOP代理
        ((CacheExampleService) AopContext.currentProxy()).getById(id);
        CONTAINER.put(id, dto);
        return dto;
    }

    @Cacheable
    public CacheExampleDTO getById(String id) {
        logger.info("getById without cache : " + id);
        return CONTAINER.get(id);
    }

    public List<CacheExampleDTO> find() {
        logger.info("find without cache");
        List<CacheExampleDTO> dtos = new ArrayList<>(CONTAINER.values());
        dtos.forEach(dto -> ((CacheExampleService) AopContext.currentProxy()).getById(dto.getId()));
        return dtos;
    }

    @CacheEvict
    public void delete(String id) {
        CONTAINER.remove(id);
    }

    @CacheEvict(allEntries = true)
    public void deleteAll() {
        CONTAINER.clear();
    }

}
