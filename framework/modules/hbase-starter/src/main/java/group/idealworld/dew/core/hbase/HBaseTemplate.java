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

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The collection of  the hbase client functions.
 *
 * @author 迹_Jason
 */
public class HBaseTemplate implements HBaseOperation {

    /**
     * HBase connection.
     */
    private Connection connection;
    /**
     * The scan of the HBase cache value. Default value is [3000].
     */
    private int scanCaching = 3000;
    /**
     * write buffer size. Default value is [3 * 1024 * 1024].
     */
    private long writeBufferSize = 3 * 1024 * 1024;

    /**
     * The hbase template construct using hbase connection.
     *
     * @param connection the connection
     */
    public HBaseTemplate(Connection connection) {
        Assert.notNull(connection, "The HBase connection is required");
        this.connection = connection;
    }

    /**
     * To action tables by tableName.
     *
     * @param tableName the target table
     * @param action    table object action
     * @return the result object of the callback action, or null
     */
    @Override
    public <T> T execute(String tableName, TableCallback<T> action) throws IOException {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        Table table = null;
        try {
            table = this.getConnection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HBaseRunTimeException(throwable);
        } finally {
            if (null != table) {
                table.close();
            }
        }
    }

    /**
     * execute table by yourselves.
     *
     * @param tableName target table
     * @param action    action
     */
    @Override
    public void execute(String tableName, MutatorCallback action) throws IOException {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = this.getConnection().getBufferedMutator(mutatorParams.writeBufferSize(getWriteBufferSize()));
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            throw new HBaseRunTimeException(throwable);
        } finally {
            if (null != mutator) {
                mutator.flush();
                mutator.close();
            }
        }
    }

    /**
     * Scans the target table, using the given column family.
     * Using the mapper param execute the row of list value.
     *
     * @param tableName target table
     * @param family    column family
     * @param mapper    row action
     * @return a list of objects mapping the scanned rows
     */
    @Override
    public <T> List<T> find(String tableName, String family, RowMapper<T> mapper) throws IOException {
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(family));
        return this.find(tableName, scan, mapper);
    }

    /**
     * Scans the target table, using the given column family and qualifier.
     * Using the mapper param execute the row of list value.
     *
     * @param tableName target table
     * @param family    column family
     * @param qualifier column qualifier
     * @param mapper    row action
     * @return a list of objects mapping the scanned rows
     */
    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, RowMapper<T> mapper) throws IOException {
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(tableName, scan, mapper);
    }

    /**
     * Scans the target table using the given {@link Scan} object.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param scan      table scanner
     * @param mapper    row action
     * @return a list of objects mapping the scanned rows
     */
    @Override
    public <T> List<T> find(String tableName, Scan scan, RowMapper<T> mapper) throws IOException {
        return this.execute(tableName, table -> {
            int caching = scan.getCaching();
            // 如果caching未设置(默认是1)，将默认配置成5000
            if (caching == 1) {
                scan.setCaching(this.getScanCaching());
            }
            try (ResultScanner scanner = table.getScanner(scan)) {
                List<T> rs = new ArrayList<>();
                int rowNum = 0;
                for (Result result : scanner) {
                    rs.add(mapper.mapRow(result, rowNum++));
                }
                return rs;
            }
        });
    }

    /**
     * Get single row by table name and row key.
     *
     * @param tableName target table
     * @param rowKey    row key
     * @param mapper    row action
     * @return object mapping the target row
     */
    @Override
    public <T> T get(String tableName, String rowKey, RowMapper<T> mapper) throws IOException {
        return this.get(tableName, rowKey, null, null, mapper);
    }

    /**
     * Get single row by table name and row key.
     *
     * @param tableName  target table
     * @param rowKey     row key
     * @param familyName column family
     * @param mapper     row mapper
     * @return object mapping the target row
     */
    @Override
    public <T> T get(String tableName, String rowKey, String familyName, RowMapper<T> mapper) throws IOException {
        return this.get(tableName, rowKey, familyName, null, mapper);
    }

    /**
     * Get single row by table name and row key.
     *
     * @param tableName  target table
     * @param rowKey     row key
     * @param familyName family
     * @param qualifier  column qualifier
     * @param mapper     mapper type
     * @return object mapping the target row
     */
    @Override
    public <T> T get(String tableName, String rowKey, String familyName, String qualifier, RowMapper<T> mapper) throws IOException {
        return this.execute(tableName, table -> {
            Get get = new Get(Bytes.toBytes(rowKey));
            if (StringUtils.isNotBlank(familyName)) {
                byte[] family = Bytes.toBytes(familyName);
                if (StringUtils.isNotBlank(qualifier)) {
                    get.addColumn(family, Bytes.toBytes(qualifier));
                } else {
                    get.addFamily(family);
                }
            }
            Result result = table.get(get);
            return mapper.mapRow(result, 0);
        });
    }

    /**
     * Save or update data with mutation.
     *
     * @param tableName target table
     * @param mutation  mutation
     */
    @Override
    public void saveOrUpdate(String tableName, Mutation mutation) throws IOException {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutation);
        });
    }

    /**
     * Save or update data with the list of mutation.
     *
     * @param tableName target table
     * @param mutations the list of mutation
     */
    @Override
    public void saveOrUpdates(String tableName, List<Mutation> mutations) throws IOException {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutations);
        });
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Sets connection.
     *
     * @param connection the connection
     * @return the connection
     */
    public HBaseTemplate setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    /**
     * Gets scan caching.
     *
     * @return the scan caching
     */
    public int getScanCaching() {
        return scanCaching;
    }

    /**
     * Sets scan caching.
     *
     * @param scanCaching the scan caching
     * @return the scan caching
     */
    public HBaseTemplate setScanCaching(int scanCaching) {
        this.scanCaching = scanCaching;
        return this;
    }

    /**
     * Gets write buffer size.
     *
     * @return the write buffer size
     */
    public long getWriteBufferSize() {
        return writeBufferSize;
    }

    /**
     * Sets write buffer size.
     *
     * @param writeBufferSize the write buffer size
     * @return the write buffer size
     */
    public HBaseTemplate setWriteBufferSize(long writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }
}
