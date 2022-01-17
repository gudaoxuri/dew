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

package group.idealworld.dew.core.dbutils.process;

import group.idealworld.dew.core.dbutils.dialect.Dialect;
import group.idealworld.dew.core.dbutils.dialect.DialectType;
import group.idealworld.dew.core.dbutils.dto.Meta;
import group.idealworld.dew.core.dbutils.dto.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DBExecutor {

    static final QueryRunner queryRunner = new QueryRunner();

    public static <E> E get(String sql, Object[] params, Class<E> clazz, Connection conn, boolean isCloseConn) throws SQLException {
        try {
            if (params == null) {
                return (E) queryRunner.query(conn, sql, new BeanHandler(clazz));
            } else {
                return (E) queryRunner.query(conn, sql, new BeanHandler(clazz), params);
            }
        } catch (SQLException e) {
            log.error("[DewDBUtils]Get error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static <E> List<E> find(String sql, Object[] params, Class<E> clazz, Connection conn, boolean isCloseConn) throws SQLException {
        try {
            if (null == params) {
                return (List<E>) queryRunner.query(conn, sql, new BeanListHandler(clazz));
            } else {
                return (List<E>) queryRunner.query(conn, sql, new BeanListHandler(clazz), params);
            }
        } catch (SQLException e) {
            log.error("[DewDBUtils]Find error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static <E> Page<E> page(String sql, Object[] params, long pageNumber, long pageSize, Class<E> clazz, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        Page<E> page = new Page<>();
        String pagedSql = dialect.paging(sql, pageNumber, pageSize);
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setRecordTotal(count(sql, params, conn, false, dialect));
        page.setPageTotal((page.getRecordTotal() + pageSize - 1) / pageSize);
        page.setObjects(find(pagedSql, params, clazz, conn, isCloseConn));
        return page;
    }

    public static Map<String, Object> get(String sql, Object[] params, Connection conn, boolean isCloseConn) throws SQLException {
        try {
            Map<String, Object> result;
            if (null == params) {
                result = queryRunner.query(conn, sql, new MapHandler());
            } else {
                result = queryRunner.query(conn, sql, new MapHandler(), params);
            }
            if (result != null) {
                Map<String, Object> lowCaseResult = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    if (entry.getValue() instanceof Clob) {
                        lowCaseResult.put(entry.getKey().toLowerCase(), convertClob((Clob) entry.getValue()));
                    } else {
                        lowCaseResult.put(entry.getKey().toLowerCase(), entry.getValue());
                    }
                }
                result = lowCaseResult;
            }
            return result;
        } catch (SQLException e) {
            log.error("[DewDBUtils]Get error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static List<Map<String, Object>> find(String sql, Object[] params, Connection conn, boolean isCloseConn) throws SQLException {
        try {
            List<Map<String, Object>> result;
            if (null == params) {
                result = queryRunner.query(conn, sql, new MapListHandler());
            } else {
                result = queryRunner.query(conn, sql, new MapListHandler(), params);
            }
            if (result != null && !result.isEmpty()) {
                List<Map<String, Object>> lowCaseResult = new ArrayList<>();
                for (Map<String, Object> item : result) {
                    Map<String, Object> lowCaseItem = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : item.entrySet()) {
                        if (entry.getValue() instanceof Clob) {
                            lowCaseItem.put(entry.getKey().toLowerCase(), convertClob((Clob) entry.getValue()));
                        } else {
                            lowCaseItem.put(entry.getKey().toLowerCase(), entry.getValue());
                        }
                    }
                    lowCaseResult.add(lowCaseItem);
                }
                result = lowCaseResult;
            }
            return result;
        } catch (SQLException e) {
            log.error("[DewDBUtils]Find error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static Page<Map<String, Object>> page(String sql, Object[] params, long pageNumber, long pageSize, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        Page<Map<String, Object>> page = new Page<>();
        String pagedSql = dialect.paging(sql, pageNumber, pageSize);
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setRecordTotal(count(sql, params, conn, false, dialect));
        page.setPageTotal((page.getRecordTotal() + pageSize - 1) / pageSize);
        page.setObjects(find(pagedSql, params, conn, isCloseConn));
        return page;
    }

    public static long count(String sql, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        return count(sql, null, conn, isCloseConn, dialect);
    }

    public static long count(String sql, Object[] params, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        String countSql = dialect.count(sql);
        try {
            if (null == params) {
                return (Long) queryRunner.query(conn, countSql, scalarHandler);
            } else {
                return (Long) queryRunner.query(conn, countSql, scalarHandler, params);
            }

        } catch (SQLException e) {
            log.error("[DewDBUtils]Count error : " + countSql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static int insert(String tableName, Map<String, Object> values,
                             Connection conn, boolean closeConnection, Dialect dialect) throws SQLException {
        String fields = String.join(",", values.keySet());
        String valueArgs = values.keySet().stream().map(f -> "?").collect(Collectors.joining(","));
        String sql = "INSERT INTO " + tableName + " (" + fields + ") VALUES (" + valueArgs + ")";
        return update(sql, values.values().toArray(), conn, closeConnection, dialect);
    }

    public static int modify(String tableName, String pkField, Object pkValue, Map<String, Object> values,
                             Connection conn, boolean closeConnection, Dialect dialect) throws SQLException {
        String set = values.keySet().stream().map(k -> k + " = ?").collect(Collectors.joining(", "));
        String sql = "UPDATE " + tableName + " SET " + set + " WHERE " + pkField + " = ? ";
        List<Object> params = new ArrayList<>(values.values());
        params.add(pkValue);
        return update(sql, params.toArray(), conn, closeConnection, dialect);
    }

    public static int update(String sql, Object[] params, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        if (dialect.getDialectType() == DialectType.HIVE && params != null) {
            throw new SQLException("SparkSQL don't support [params] parameter.");
        }
        try {
            if (null == params) {
                return queryRunner.update(conn, sql);
            } else {
                return queryRunner.update(conn, sql, params);
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("[DewDBUtils]Connection error : " + sql, e1);
                throw e1;
            }
            log.error("[DewDBUtils]Update error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static void batch(Map<String, Object[]> sqls, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        if (dialect.getDialectType() == DialectType.HIVE) {
            throw new SQLException("SparkSQL don't support [batch] method.");
        }
        for (Map.Entry<String, Object[]> entry : sqls.entrySet()) {
            try {
                if (null == entry.getValue()) {
                    queryRunner.update(conn, entry.getKey());
                } else {
                    queryRunner.update(conn, entry.getKey(), entry.getValue());
                }
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    log.error("[DewDBUtils]Connection error : " + entry.getKey(), e1);
                    throw e1;
                }
                log.error("[DewDBUtils]Batch error : " + entry.getKey(), e);
                throw e;
            } finally {
                if (isCloseConn) {
                    closeConnection(conn);
                }
            }
        }
    }

    public static int[] batch(String sql, Object[][] params, Connection conn, boolean isCloseConn, Dialect dialect) throws SQLException {
        if (dialect.getDialectType() == DialectType.HIVE) {
            throw new SQLException("SparkSQL don't support [batch] method.");
        }
        try {
            return queryRunner.batch(conn, sql, params);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("[DewDBUtils]Connection error : " + sql, e1);
                throw e1;
            }
            log.error("[DewDBUtils]Batch error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    public static List<Meta> getMetaData(String tableName, Connection conn) throws SQLException {
        return findMetaData(tableName, null, conn);
    }

    public static Meta getMetaData(String tableName, String fieldName, Connection conn) throws SQLException {
        List<Meta> metas = findMetaData(tableName, fieldName, conn);
        if (metas.size() == 1) {
            return metas.get(0);
        }
        return null;
    }

    private static List<Meta> findMetaData(String tableName, String fieldName, Connection conn) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE 1=2");
            rs = st.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            List<Meta> metas = new ArrayList<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String columnName = meta.getColumnName(i).substring(meta.getColumnName(i).lastIndexOf(".") + 1);
                String columnLabel = meta.getColumnLabel(i).substring(meta.getColumnLabel(i).lastIndexOf(".") + 1);
                if (null != fieldName && !columnLabel.equalsIgnoreCase(fieldName)) {
                    continue;
                }
                metas.add(new Meta(meta.getColumnType(i), columnName.toLowerCase(), columnLabel.toLowerCase()));
            }
            return metas;
        } catch (SQLException e) {
            log.error("[DewDBUtils]getResultSet error : " + tableName, e);
            throw e;
        } finally {
            if (null != rs) {
                rs.close();
            }
            if (null != st) {
                st.close();
            }
            closeConnection(conn);
        }
    }

    public static void ddl(String sql, Connection conn, boolean isCloseConn) throws SQLException {
        try {
            log.trace("[DewDBUtils]Execute DDL : " + sql);
            queryRunner.update(conn, sql);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                log.error("[DewDBUtils]Connection error : " + sql, e1);
                throw e1;
            }
            log.error("[DewDBUtils]ddl error : " + sql, e);
            throw e;
        } finally {
            if (isCloseConn) {
                closeConnection(conn);
            }
        }
    }

    private static void closeConnection(Connection conn) throws SQLException {
        if (null != conn && !conn.isClosed()) {
            try {
                log.trace("[DewDBUtils]Close connection:" + conn.toString());
                conn.close();
            } catch (SQLException e) {
                log.error("[DewDBUtils]Close transactionConnection error : ", e);
                throw e;
            }
        }
    }

    private static ScalarHandler scalarHandler = new ScalarHandler() {
        @Override
        public Object handle(ResultSet rs) throws SQLException {
            Object obj = super.handle(rs);
            if (obj instanceof BigDecimal) {
                return ((BigDecimal) obj).longValue();
            } else if (obj instanceof Long) {
                return obj;
            } else {
                return ((Number) obj).longValue();
            }
        }
    };

    @SneakyThrows
    private static String convertClob(Clob clob) {
        StringBuilder value = new StringBuilder();
        String line;
        if (clob != null) {
            Reader reader = clob.getCharacterStream();
            BufferedReader br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                value.append(line).append("\r\n");
            }
        }
        if (value.length() >= 2) {
            return value.substring(0, value.length() - 2);
        } else {
            return "";
        }
    }

}
