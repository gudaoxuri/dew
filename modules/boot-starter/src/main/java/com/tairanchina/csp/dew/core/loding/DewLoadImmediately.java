package com.tairanchina.csp.dew.core.loding;

import java.lang.annotation.*;

/**
 * Dew专用的Bean加载注解，在Dew.class初始化立即加载对应的Bean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DewLoadImmediately {

}
