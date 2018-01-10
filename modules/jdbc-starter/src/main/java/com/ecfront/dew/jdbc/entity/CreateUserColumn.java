package com.ecfront.dew.jdbc.entity;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CreateUserColumn {

    // 默认为字段名（驼峰转下划线）
    String columnName() default "";

}
