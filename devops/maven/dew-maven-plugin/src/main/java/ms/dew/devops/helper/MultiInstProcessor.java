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

public abstract class MultiInstProcessor {

    protected static final ConcurrentHashMap<String, String> EXISTS = new ConcurrentHashMap<>();
    protected static final Map<String, Object> INSTANCES = new HashMap<>();

    protected static <T> void multiInit(String instanceId, Supplier<T> initFun, String... hashItems) {
        String hashStr = String.join("-", hashItems);
        try {
            String hash = $.security.digest.digest(hashStr, "MD5");
            if (EXISTS.containsKey(hash)) {
                INSTANCES.put(instanceId, INSTANCES.get(EXISTS.get(hash)));
                return;
            }
            EXISTS.put(hash, instanceId);
        } catch (NoSuchAlgorithmException ignore) {
        }
        INSTANCES.put(instanceId, initFun.get());
    }

    protected static <T> T multiInst(String instanceId) {
        return (T) INSTANCES.get(instanceId);
    }

}
