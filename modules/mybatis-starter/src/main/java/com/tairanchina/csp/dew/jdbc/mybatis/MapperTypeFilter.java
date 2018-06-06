package com.tairanchina.csp.dew.jdbc.mybatis;

import com.tairanchina.csp.dew.jdbc.mybatis.annotion.DS;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;


public class MapperTypeFilter extends AbstractTypeHierarchyTraversingFilter {


    private String dataSource;

    public MapperTypeFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, true, false);
    }

    public MapperTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
        this(annotationType, considerMetaAnnotations, false);
    }

    private MapperTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, boolean considerInterfaces) {
        super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return matchSelf(metadataReader);
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader) {
        Class mapper = null;
        try {
            mapper = Class.forName(metadataReader.getClassMetadata().getClassName());
            DS annotation = (DS) mapper.getAnnotation(DS.class);
            return annotation.dataSource().equals(dataSource);
        } catch (Exception e) {
            logger.error(metadataReader.getClassMetadata().getClassName() + " is not annotationed by DS");
            return false;
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    MapperTypeFilter setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }
}
