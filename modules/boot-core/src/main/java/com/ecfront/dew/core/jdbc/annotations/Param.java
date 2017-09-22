package com.ecfront.dew.core.jdbc.annotations;

import java.lang.annotation.*;

/**
 * Created by 迹_Jason on 2017/7/26.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value() default "";
}
