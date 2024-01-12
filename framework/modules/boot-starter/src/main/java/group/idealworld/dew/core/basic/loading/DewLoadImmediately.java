package group.idealworld.dew.core.basic.loading;

import java.lang.annotation.*;

/**
 * Dew专用的Bean加载注解，在Dew.class初始化立即加载对应的Bean.
 *
 * @author gudaoxuri
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DewLoadImmediately {

    /**
     * 加载优先级.
     * <p>
     * 暂未实现
     *
     * @return 优先级
     */
    int value() default 10;

}
