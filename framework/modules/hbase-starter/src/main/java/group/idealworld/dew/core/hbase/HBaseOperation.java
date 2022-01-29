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

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Scan;

import java.io.IOException;
import java.util.List;

/**
 * Created on 2019/5/5.
 *
 * @author 迹_Jason
 */
public interface HBaseOperation {

    /**
     * To action tables by tableName.
     *
     * @param <T>       the type parameter
     * @param tableName the target table
     * @param action    table object action
     * @return the result object of the callback action, or null
     * @throws IOException the io exception
     */
    <T> T execute(String tableName, TableCallback<T> action) throws IOException;

    /**
     * execute table by yourselves.
     *
     * @param tableName target table
     * @param action    action
     * @throws IOException the io exception
     */
    void execute(String tableName, MutatorCallback action) throws IOException;

    /**
     * Scans the target table, using the given column family.
     * Using the mapper param execute the row of list value.
     *
     * @param <T>       the type parameter
     * @param tableName target table
     * @param family    column family
     * @param mapper    row action
     * @return a list of objects mapping the scanned rows
     * @throws IOException the io exception
     */
    <T> List<T> find(String tableName, String family, final RowMapper<T> mapper) throws IOException;

    /**
     * Scans the target table, using the given column family and qualifier.
     * Using the mapper param execute the row of list value.
     *
     * @param <T>       the type parameter
     * @param tableName target table
     * @param family    column family
     * @param qualifier column qualifier
     * @param mapper    row action
     * @return a list of objects mapping the scanned rows
     * @throws IOException the io exception
     */
    <T> List<T> find(String tableName, String family, String qualifier, final RowMapper<T> mapper) throws IOException;

    /**
     * Scans the target table using the given {@link Scan} object.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param <T>       the type parameter
     * @param tableName target table
     * @param scan      table scanner
     * @param mapper    row action
     * @return a list of objects mapping the scanned rows
     * @throws IOException the io exception
     */
    <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> mapper) throws IOException;

    /**
     * Get single row by table name and row key.
     *
     * @param <T>       the type parameter
     * @param tableName target table
     * @param rowKey    row key
     * @param mapper    row action
     * @return object mapping the target row
     * @throws IOException the io exception
     */
    <T> T get(String tableName, String rowKey, final RowMapper<T> mapper) throws IOException;

    /**
     * Get single row by table name and row key.
     *
     * @param <T>        the type parameter
     * @param tableName  target table
     * @param rowKey     row key
     * @param familyName column family
     * @param mapper     row mapper
     * @return object mapping the target row
     * @throws IOException the io exception
     */
    <T> T get(String tableName, String rowKey, String familyName, final RowMapper<T> mapper) throws IOException;

    /**
     * Get single row by table name and row key.
     *
     * @param <T>        the type parameter
     * @param tableName  target table
     * @param rowKey     row key
     * @param familyName family
     * @param qualifier  column qualifier
     * @param mapper     mapper type
     * @return object mapping the target row
     * @throws IOException the io exception
     */
    <T> T get(String tableName, final String rowKey, final String familyName, final String qualifier, final RowMapper<T> mapper) throws IOException;

    /**
     * Save or update data with mutation.
     *
     * @param tableName target table
     * @param mutation  mutation
     * @throws IOException the io exception
     */
    void saveOrUpdate(String tableName, Mutation mutation) throws IOException;

    /**
     * Save or update data with the list of mutation.
     *
     * @param tableName target table
     * @param mutations the list of mutation
     * @throws IOException the io exception
     */
    void saveOrUpdates(String tableName, List<Mutation> mutations) throws IOException;

}
