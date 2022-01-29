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
