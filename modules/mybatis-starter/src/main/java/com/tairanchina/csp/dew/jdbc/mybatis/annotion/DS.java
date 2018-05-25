package com.tairanchina.csp.dew.jdbc.mybatis.annotion;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DS {
    String dataSource() default "primary";
}
