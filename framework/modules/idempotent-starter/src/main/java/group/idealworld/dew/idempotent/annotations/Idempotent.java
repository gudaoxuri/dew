package group.idealworld.dew.idempotent.annotations;

import group.idealworld.dew.idempotent.strategy.StrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Idempotent.
 *
 * @author gudaoxuri
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Idempotent {

    /**
     * 指定幂等操作ID标识，可以位于HTTP Header或请求参数中.
     *
     * @return 设置的幂等操作ID标识
     */
    String optIdFlag() default "";

    /**
     * 设置过期时间，单位毫秒.
     *
     * @return 设置的过期时间
     */
    long expireMs() default 1000 * 60 * 60;

    /**
     * 设置默认策略.
     *
     * @return 设置的策略
     */
    StrategyEnum strategy() default StrategyEnum.AUTO;

    /**
     * 设置是否需要显式确认.
     *
     * @return 是否需要确认
     */
    boolean needConfirm() default true;

}
