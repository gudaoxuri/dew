package com.ecfront.dew.jdbc;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by 迹_Jason on 2017/7/26.
 * Dao扫描
 */
public class ClassPathScanner extends ClassPathBeanDefinitionScanner {

    public ClassPathScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        // scan interface beanDefinitions by spring scanner
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No dao was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            // add interface own character
            for (BeanDefinitionHolder holder : beanDefinitions) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
                definition.getPropertyValues().add("mapperInterface", definition.getBeanClassName());
                definition.setBeanClass(DaoFactoryBean.class);
                definition.setScope(BeanDefinition.SCOPE_SINGLETON);
            }
        }
        return beanDefinitions;
    }

    public void registerFilters() {
        // default include filter that accepts all classes
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }

}
