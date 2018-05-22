package com.tairanchina.csp.dew.idempotent.annotations;

import com.tairanchina.csp.dew.idempotent.strategy.StrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Idempotent {

    /**
     * 指定幂等操作类型标识，可以位于HTTP Header或请求参数中，要求全局唯一
     */
    String optTypeFlag() default "";

    /**
     * 指定幂等操作ID标识，可以位于HTTP Header或请求参数中
     */
    String optIdFlag() default "";

    /**
     * 设置过期时间，单位毫秒
     */
    long expireMs() default -1;

    /**
     * 设置默认策略
     */
    StrategyEnum strategy() default StrategyEnum.AUTO;

    /**
     * 设置是否需要显式确认
     */
    boolean needConfirm() default true;

}
