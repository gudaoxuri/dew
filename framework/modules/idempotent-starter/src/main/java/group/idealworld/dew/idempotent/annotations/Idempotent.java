/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
