package com.ecfront.dew.jdbc.entity;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {

    // 默认为类名（驼峰转下划线）
    String tableName() default "";

}
