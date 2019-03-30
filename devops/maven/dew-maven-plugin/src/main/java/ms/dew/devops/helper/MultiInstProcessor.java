/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.devops.helper;

import com.ecfront.dew.common.$;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 多实例处理，支持多个实例Id共享同一实例对象.
 *
 * @author gudaoxuri
 */
public abstract class MultiInstProcessor {

    protected static final ConcurrentHashMap<String, String> EXISTS = new ConcurrentHashMap<>();
    protected static final Map<String, Object> INSTANCES = new HashMap<>();

    /**
     * 初始化多实例.
     * <p>
     * 实例类型+实例Id 全局唯一，通过 <tt>hashItems<tt> 区别是否共享实例对象
     *
     * @param kind       实例类型
     * @param instanceId 实例Id
     * @param initFun    初始化方法
     * @param hashItems  相同实例判定
     * @param <T>        实例Type
     */
    protected static <T> void multiInit(String kind, String instanceId, Supplier<T> initFun, String... hashItems) {
        if (INSTANCES.containsKey(kind + "-" + instanceId)) {
            return;
        }
        String hashStr = kind + String.join("-", hashItems);
        try {
            String hash = $.security.digest.digest(hashStr, "MD5");
            if (EXISTS.containsKey(hash)) {
                INSTANCES.put(kind + "-" + instanceId, INSTANCES.get(EXISTS.get(hash)));
                return;
            }
            EXISTS.put(hash, kind + "-" + instanceId);
        } catch (NoSuchAlgorithmException ignore) {
        }
        INSTANCES.put(kind + "-" + instanceId, initFun.get());
    }

    /**
     * 初始化多实例.
     * <p>
     * 不判断实例对象是否共享，多用于Mock场景
     *
     * @param kind       实例类型
     * @param instanceId 实例Id
     * @param InstObj    实例对象
     * @param <T>        实例Type
     */
    public static <T> void forceInit(String kind, String instanceId, T InstObj) {
        INSTANCES.put(kind + "-" + instanceId, InstObj);
    }

    /**
     * 获取实例对象.
     *
     * @param kind       实例类型
     * @param instanceId 实例Id
     * @param <T>        实例Type
     * @return 实例对象
     */
    protected static <T> T multiInst(String kind, String instanceId) {
        return (T) INSTANCES.get(kind + "-" + instanceId);
    }

}
