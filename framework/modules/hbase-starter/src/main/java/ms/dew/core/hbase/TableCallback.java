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

package ms.dew.core.hbase;

import org.apache.hadoop.hbase.client.Table;

/**
 * The hbase table action.
 *
 * @param <T> the type parameter
 * @author 迹_Jason
 */
@FunctionalInterface
public interface TableCallback<T> {
    /**
     * The hbase table action function.
     *
     * @param table table object
     * @return hope return object
     * @throws Throwable exception
     */
    T doInTable(Table table) throws Throwable;
}
