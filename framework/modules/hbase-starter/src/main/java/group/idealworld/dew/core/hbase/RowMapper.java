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

package group.idealworld.dew.core.hbase;

import org.apache.hadoop.hbase.client.Result;

/**
 * The row mapper of the data that hbase data.
 *
 * @param <T> the type parameter
 * @author 迹_Jason
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * The row action function.
     *
     * @param result hbase data
     * @param rowNum row number
     * @return T hope return object.
     * @throws Exception exception
     */
    T mapRow(Result result, int rowNum) throws Exception;
}
