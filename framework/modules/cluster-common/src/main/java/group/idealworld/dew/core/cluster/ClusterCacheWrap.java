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

package group.idealworld.dew.core.cluster;

/**
 * 缓存服务多实例封装.
 *
 * @author gudaoxuri
 */
public interface ClusterCacheWrap {

    /**
     * 缓存服务实例获取，默认实例.
     *
     * @return 缓存服务实例
     */
    default ClusterCache instance() {
        return instance("");
    }

    /**
     * 缓存服务服务实例获取.
     *
     * @param key 实例Key
     * @return 缓存服务实例
     */
    ClusterCache instance(String key);

    /**
     * 是否存在对应的缓存服务实例.
     *
     * @param key 实例Key
     * @return 是否存在
     */
    boolean exist(String key);

}
