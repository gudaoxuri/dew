package com.tairanchina.csp.dew.jdbc.mybatis.annotion;

import com.tairanchina.csp.dew.jdbc.mybatis.MybatisStarterConfiguration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@DependsOn("dew")
@Import({DewMapperScannerRegister.class, MybatisStarterConfiguration.class})
public @interface DewMapperScan {

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;
}
