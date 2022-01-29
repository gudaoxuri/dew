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
 * 分布式Map服务多实例封装.
 *
 * @author gudaoxuri
 */
public interface ClusterMapWrap {

    /**
     * 分布式Map服务实例获取.
     *
     * @param key     实例Key
     * @param clazz   值的类型
     * @param <M> 值的类型
     * @return 分布式Map服务实例
     */
    <M> ClusterMap<M> instance(String key, Class<M> clazz);

}
