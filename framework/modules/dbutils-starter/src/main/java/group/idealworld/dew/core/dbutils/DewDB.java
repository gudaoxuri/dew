/*
 * Copyright 2020. the original author or authors.
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
package group.idealworld.dew.core.dbutils;

import group.idealworld.dew.core.dbutils.dto.Meta;
import group.idealworld.dew.core.dbutils.dto.Page;
import group.idealworld.dew.core.dbutils.process.DBExecutor;
import group.idealworld.dew.core.dbutils.process.DSLoader;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据操作类.
 *
 * @author gudaoxuri
 */
public class DewDB {

    private static final Logger logger = LoggerFactory.getLogger(DewDB.class);

    private static final Map<String, DewDB> DBS = new HashMap<>();
    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    private final DSLoader.DSInfo dsInfo;

    public static DewDB pick(String dsCode) {
        if (!DBS.containsKey(dsCode)) {
            synchronized (DBS) {
                if (!DBS.containsKey(dsCode)) {
                    DBS.put(dsCode, new DewDB(DSLoader.getDSInfo(dsCode)));
                }
            }
        }
        return DBS.get(dsCode);
    }

    DewDB(DSLoader.DSInfo dsInfo) {
        this.dsInfo = dsInfo;
    }

    public DSLoader.DSInfo getDsInfo() {
        return dsInfo;
    }

    /**
     * 创建表.
     * <p>
     *
     * @param tableName    表名
     * @param tableDesc    表说明
     * @param fields       表字段（字段名 - 类型）
     * @param fieldsDesc   字段说明
     * @param indexFields  索引字段
     * @param uniqueFields 唯一值字段
     * @param pkField      主键字段
     * @throws SQLException SQL错误
     * @deprecated - 此功能存在一定限制，建议使用 {@link #ddl(String)} 建表
     */
    @Deprecated
    public void createTableIfNotExist(String tableName, String tableDesc,
                                      Map<String, String> fields,
                                      Map<String, String> fieldsDesc,
                                      List<String> indexFields,
                                      List<String> uniqueFields,
                                      String pkField) throws SQLException {
        tableName = tableName.toLowerCase();
        DBExecutor.ddl(
                dsInfo.getDialect().createTableIfNotExist(tableName, tableDesc,
                        fields, fieldsDesc, indexFields, uniqueFields, pkField),
                getConnection(), isCloseConnection()
        );
    }

    /**
     * DDL操作.
     *
     * @param ddl DDL语句
     * @throws SQLException SQL错误
     */
    public void ddl(String ddl) throws SQLException {
        DBExecutor.ddl(ddl, getConnection(), isCloseConnection());
    }

    /**
     * 获取单条记录.
     *
     * @param tableName 表名
     * @param pkField   主键字段
     * @param pkValue   主键值
     * @param clazz     对象类
     * @param <E>       对象
     * @return java对象
     * @throws SQLException SQL错误
     */
    public <E> E getByPk(String tableName, String pkField, Object pkValue, Class<E> clazz) throws SQLException {
        return get("SELECT * FROM " + tableName + " WHERE " + pkField + " = ?", clazz, pkValue);
    }

    /**
     * 获取单个对象.
     *
     * @param sql    SQL
     * @param params 参数
     * @param clazz  对象类
     * @param <E>    对象
     * @return java对象
     * @throws SQLException SQL错误
     */
    public <E> E get(String sql, Class<E> clazz, Object... params) throws SQLException {
        return DBExecutor.get(sql, params, clazz, getConnection(), isCloseConnection());
    }

    /**
     * 获取多个对象.
     *
     * @param sql    SQL
     * @param params 参数
     * @param clazz  对象类
     * @param <E>    对象
     * @return java对象
     * @throws SQLException SQL错误
     */
    public <E> List<E> find(String sql, Class<E> clazz, Object... params) throws SQLException {
        return DBExecutor.find(sql, params, clazz, getConnection(), isCloseConnection());
    }


    /**
     * 获取多个对象（带分页）.
     *
     * @param sql        SQL
     * @param params     参数
     * @param pageNumber 页码（从1开始）
     * @param pageSize   每页条数
     * @param clazz      对象类
     * @param <E>        对象
     * @return 多个对象（带分页）
     * @throws SQLException SQL错误
     */
    public <E> Page<E> page(String sql, long pageNumber, long pageSize, Class<E> clazz, Object... params) throws SQLException {
        return DBExecutor.page(sql, params, pageNumber, pageSize, clazz, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 判断记录是否存在.
     *
     * @param tableName 表名
     * @param pkField   主键字段
     * @param pkValue   主键值
     * @return 是否存在
     * @throws SQLException SQL错误
     */
    public boolean exits(String tableName, String pkField, Object pkValue) throws SQLException {
        return get("SELECT id FROM " + tableName + " WHERE " + pkField + " = ?", new Object[]{pkValue}).size() != 0;
    }

    /**
     * 判断记录是否存在.
     *
     * @param sql    SQL
     * @param params 参数
     * @return 是否存在
     * @throws SQLException SQL错误
     */
    public boolean exits(String sql, Object... params) throws SQLException {
        return count(sql, params) != 0;
    }

    /**
     * 获取单条记录.
     *
     * @param tableName 表名
     * @param pkField   主键字段
     * @param pkValue   主键值
     * @return 单条记录
     * @throws SQLException SQL错误
     */
    public Map<String, Object> getByPk(String tableName, String pkField, Object pkValue) throws SQLException {
        return get("SELECT * FROM " + tableName + " WHERE " + pkField + " = ?", pkValue);
    }

    /**
     * 获取单条记录.
     *
     * @param sql    SQL
     * @param params 参数
     * @return 单条记录
     * @throws SQLException SQL错误
     */
    public Map<String, Object> get(String sql, Object... params) throws SQLException {
        return DBExecutor.get(sql, params, getConnection(), isCloseConnection());
    }

    /**
     * 获取多条记录.
     *
     * @param sql    SQL
     * @param params 参数
     * @return 多条记录（带分页）
     * @throws SQLException SQL错误
     */
    public List<Map<String, Object>> find(String sql, Object... params) throws SQLException {
        return DBExecutor.find(sql, params, getConnection(), isCloseConnection());
    }

    /**
     * 获取多条记录（带分页）.
     *
     * @param sql        SQL
     * @param params     参数
     * @param pageNumber 页码（从1开始）
     * @param pageSize   每页条数
     * @return 多条记录（带分页）
     * @throws SQLException SQL错误
     */
    public Page<Map<String, Object>> page(String sql, int pageNumber, int pageSize, Object... params) throws SQLException {
        return DBExecutor.page(sql, params, pageNumber, pageSize, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 获取记录数.
     *
     * @param sql    SQL
     * @param params 参数
     * @return 记录数
     * @throws SQLException SQL错误
     */
    public long count(String sql, Object... params) throws SQLException {
        return DBExecutor.count(sql, params, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 添加记录.
     *
     * @param tableName 表名
     * @param values    值列表
     * @return 影响行数
     * @throws SQLException SQL错误
     */
    public int insert(String tableName, Map<String, Object> values) throws SQLException {
        return DBExecutor.insert(tableName, values, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 修改记录.
     *
     * @param tableName 表名
     * @param pkField   主键字段
     * @param pkValue   主键值
     * @param values    值列表
     * @return 影响行数
     * @throws SQLException SQL错误
     */
    public int modify(String tableName, String pkField, Object pkValue, Map<String, Object> values) throws SQLException {
        return DBExecutor.modify(tableName, pkField, pkValue, values, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 更新记录.
     *
     * @param sql    SQL
     * @param params 参数
     * @return 影响行数
     * @throws SQLException SQL错误
     */
    public int update(String sql, Object... params) throws SQLException {
        return DBExecutor.update(sql, params, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 批量更新记录.
     *
     * @param sql    SQL
     * @param params 参数
     * @return 影响行数
     * @throws SQLException SQL错误
     */
    public int[] batch(String sql, Object[][] params) throws SQLException {
        return DBExecutor.batch(sql, params, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 批量更新记录.
     *
     * @param sqls SQL
     * @throws SQLException SQL错误
     */
    public void batch(Map<String, Object[]> sqls) throws SQLException {
        DBExecutor.batch(sqls, getConnection(), isCloseConnection(), dsInfo.getDialect());
    }

    /**
     * 删除单条记录.
     *
     * @param tableName 表名
     * @param pkField   主键字段
     * @param pkValue   主键值
     * @return 影响行数
     * @throws SQLException SQL错误
     */
    public Integer delete(String tableName, String pkField, Object pkValue) throws SQLException {
        return update("DELETE FROM " + tableName + " WHERE " + pkField + " = ?", pkValue);
    }

    /**
     * 删除所有记录.
     *
     * @param tableName 表名
     * @return 单条记录
     * @throws SQLException SQL错误
     */
    public Integer deleteAll(String tableName) throws SQLException {
        return update("DELETE FROM " + tableName);
    }

    /**
     * 获取Meta信息.
     *
     * @param tableName 表名
     * @return Meta信息
     * @throws SQLException SQL错误
     */
    public List<Meta> getMetaData(String tableName) throws SQLException {
        return DBExecutor.getMetaData(tableName, getConnection());
    }

    /**
     * 获取Meta信息.
     *
     * @param tableName 表名
     * @param fieldName 指定的字段名
     * @return Meta信息
     * @throws SQLException SQL错误
     */
    public Meta getMetaData(String tableName, String fieldName) throws SQLException {
        return DBExecutor.getMetaData(tableName, fieldName, getConnection());
    }

    /**
     * 打开事务.
     */
    public void open() {
        Connection conn = threadLocalConnection.get();
        try {
            if (null == conn) {
                conn = getConnection();
                threadLocalConnection.set(conn);
            }
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error("[DewDBUtils]Connection open error", e);
        }
    }

    /**
     * 提交事务.
     */
    public void commit() {
        Connection conn = threadLocalConnection.get();
        if (null != conn) {
            try {
                conn.commit();
            } catch (SQLException e) {
                logger.error("[DewDBUtils]Connection commit error", e);
            }
        }
        close();
    }

    /**
     * 显式回滚事务.
     * <p>
     * 发生SQL错误时会自动回滚，但业务错误需要调用此方法手工回滚.
     */
    public void rollback() {
        Connection conn = threadLocalConnection.get();
        if (null != conn) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("[DewDBUtils]Connection rollback error", e);
            }
        }
        close();
    }

    private boolean isCloseConnection() {
        return null == threadLocalConnection.get();
    }

    private void close() {
        Connection conn = threadLocalConnection.get();
        if (null != conn) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
                threadLocalConnection.set(null);
            } catch (SQLException e) {
                logger.error("[DewDBUtils]Connection close error", e);
            }
        }
    }

    @SneakyThrows
    private Connection getConnection() {
        try {
            if (threadLocalConnection.get() != null && !threadLocalConnection.get().isClosed()) {
                return threadLocalConnection.get();
            }
            Connection conn = dsInfo.getDataSource().getConnection();
            if (!conn.isClosed()) {
                return conn;
            }
            //Re-setting connection when connection was close.
            synchronized (DSLoader.class) {
                logger.warn("[DewDBUtils]Connection info [{}] was close", conn.toString());
                DSLoader.loadPool(dsInfo.getDsConfig(), dsInfo.getDialect());
                return dsInfo.getDataSource().getConnection();
            }
        } catch (SQLException e) {
            logger.error("[DewDBUtils]Connection get error.", e);
            throw e;
        }
    }

}
