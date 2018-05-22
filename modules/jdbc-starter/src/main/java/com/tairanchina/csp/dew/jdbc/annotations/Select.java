package com.tairanchina.csp.dew.jdbc.annotations;

import java.lang.annotation.*;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value() default "";
    Class<?> entityClass() default Map.class;
}
