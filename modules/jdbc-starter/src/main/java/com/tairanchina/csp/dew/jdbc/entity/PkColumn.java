package com.tairanchina.csp.dew.jdbc.entity;

import java.lang.annotation.*;

/**
 * 标识主键字段，支持int/String格式
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PkColumn {

    // 默认为类名（驼峰转下划线）
    String columnName() default "";

    boolean uuid() default false;

}
