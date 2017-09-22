package com.ecfront.dew.core.jdbc.annotations;

import java.lang.annotation.*;
import java.util.Map;

/**
 * Created by 迹_Jason on 2017/7/21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value() default "";
    Class<?> entityClass() default Map.class;
}
