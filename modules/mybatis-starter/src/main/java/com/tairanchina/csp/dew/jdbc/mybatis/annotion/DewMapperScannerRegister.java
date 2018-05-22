package com.tairanchina.csp.dew.jdbc.mybatis.annotion;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.jdbc.mybatis.MapperScaner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DewMapperScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private Set<String> dataSources = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(DewMapperScannerRegister.class);

    private ResourceLoader resourceLoader;

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取DewMapperScanner字段值
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(DewMapperScan.class.getName()));

        // 获取被DS注解的类
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            Set<Class<?>> mappers = null;
            try {
                mappers = $.clazz.scan(pkg, new HashSet<Class<? extends Annotation>>() {{
                    add(DS.class);
                }}, null);
                if (mappers == null || mappers.isEmpty()) {
                    continue;
                }
            } catch (Exception e) {
                logger.error("mapper init failed");
                continue;
            }
            mappers.forEach(c -> {
                if (c.getAnnotation(DS.class).isSharding()) {
                    dataSources.add("sharding");
                } else {
                    dataSources.add(c.getAnnotation(DS.class).dataSource());
                }

            });
        }
        for (String dataSource : dataSources) {
            scan(new MapperScaner(registry).setDataSource(dataSource), annoAttrs);
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void scan(MapperScaner scanner, AnnotationAttributes annoAttrs) {
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
        }
        List<String> basePackages = new ArrayList<>();
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

}
