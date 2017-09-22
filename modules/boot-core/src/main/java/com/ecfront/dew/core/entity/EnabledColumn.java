package com.ecfront.dew.core.entity;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnabledColumn {

    // 默认为字段名（驼峰转下划线）
    String columnName() default "";

    /**
     * 为true 反转结果含义，true->false，false->true
     * 正常对应的字段名可以是 enabled , 调用 enableById, 结果为true，表示启用
     * 但对应的字段名也可以是 del_flag, 调用 enableById, 结果应该为false，将reverse设置成false即可实现
     */
    boolean reverse() default false;

}
