package com.tairanchina.csp.dew.jdbc;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.phoenix.parser.PhoenixStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.StandardCode;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.jdbc.DS;
import com.tairanchina.csp.dew.core.jdbc.SB;
import com.tairanchina.csp.dew.jdbc.dialect.Dialect;
import com.tairanchina.csp.dew.jdbc.dialect.DialectFactory;
import com.tairanchina.csp.dew.jdbc.dialect.DialectType;
import com.tairanchina.csp.dew.jdbc.entity.EntityContainer;
import com.tairanchina.csp.dew.jdbc.proxy.MethodConstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DewDS implements DS {

    private static final Logger logger = LoggerFactory.getLogger(DewDS.class);

    private static final Pattern FIELD_PLACE_HOLDER_PATTERN = Pattern.compile("\\#\\{\\s*\\w+\\s*\\}"); // 正则匹配 #{key}

    private static final char UNDERLINE = '_';
    private static final String STAR = "*";
    private static final String POINT = "";
    private static final String EMPTY = "";
    private String leftDecorated;
    private String rightDecorated;
    private JdbcTemplate jdbcTemplate;
    private String jdbcUrl;

    private Dialect dialect;

    private void init() {
        dialect = DialectFactory.parseDialect(jdbcUrl);
        switch (dialect.getDialectType()) {
            case H2:
                leftDecorated = "`";
                rightDecorated = "`";
                break;
            case MYSQL:
                leftDecorated = "`";
                rightDecorated = "`";
                break;
            case ORACLE:
                leftDecorated = "\"";
                rightDecorated = "\"";
                break;
            case POSTGRE:
                leftDecorated = "\"";
                rightDecorated = "\"";
                break;
            case SQLSERVER:
                leftDecorated = "[";
                rightDecorated = "]";
                break;
            case DB2:
                leftDecorated = "[";
                rightDecorated = "]";
                break;
            case PHOENIX:
                leftDecorated = "[";
                rightDecorated = "]";
                break;
            default:
        }
    }

    public JdbcTemplate jdbc() {
        return jdbcTemplate;
    }

    @Override
    public Object insert(Object entity) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entity.getClass());
        Object[] packageInsert = packageInsert(new ArrayList<Object>() {{
            add(entity);
        }}, true);
        String sql = (String) packageInsert[0];
        Object[] args = ((List<Object[]>) packageInsert[1]).get(0);
        if (entityClassInfo.pkFieldNameOpt.isPresent() &&
                entityClassInfo.pkUUIDOpt.isPresent() && entityClassInfo.pkUUIDOpt.get()) {
            jdbcTemplate.update(sql, args);
            return ((Optional<String>) packageInsert[2]).get();
        } else if (entityClassInfo.pkFieldNameOpt.isPresent()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{entityClassInfo.pkFieldNameOpt.get()});
                PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(args);
                pss.setValues(ps);
                return ps;
            }, keyHolder);
            return keyHolder.getKey();
        } else {
            jdbcTemplate.update(sql, args);
            return 0;
        }
    }

    @Override
    public void insert(Iterable<?> entities) {
        Object[] packageInsert = packageInsert(entities, false);
        jdbcTemplate.batchUpdate((String) packageInsert[0], (List<Object[]>) packageInsert[1]);
    }

    @Override
    public int updateById(Object id, Object entity) {
        try {
            $.bean.setValue(entity, EntityContainer.getEntityClassByClazz(entity.getClass()).pkFieldNameOpt.get(), id);
            Object[] packageUpdate = packageUpdate(entity, true);
            return jdbcTemplate.update((String) packageUpdate[0], (Object[]) packageUpdate[1]);
        } catch (NoSuchFieldException e) {
            logger.error("UpdateById error", e);
            return 0;
        }
    }

    @Override
    public int updateByCode(String code, Object entity) {
        try {
            $.bean.setValue(entity, EntityContainer.getEntityClassByClazz(entity.getClass()).codeFieldNameOpt.get(), code);
            Object[] packageUpdate = packageUpdate(entity, true);
            return jdbcTemplate.update((String) packageUpdate[0], (Object[]) packageUpdate[1]);
        } catch (NoSuchFieldException e) {
            logger.error("updateByCode error", e);
            return 0;
        }
    }

    @Override
    public <E> E getById(Object id, Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return get(DewSB.inst()
                .eq(entityClassInfo.pkFieldNameOpt.get(), id), entityClazz);
    }

    @Override
    public <E> E getByCode(String code, Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return get(DewSB.inst()
                .eq(entityClassInfo.codeFieldNameOpt.get(), code), entityClazz);
    }

    @Override
    public <E> E get(SB sqlBuilder, Class<E> entityClazz) {
        Object[] packageSelect = packageSelect(entityClazz, sqlBuilder);
        return get((String) packageSelect[0], (Object[]) packageSelect[1], entityClazz);
    }

    @Override
    public <E> E get(String sql, Object[] params, Class<E> entityClazz) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, (Object[]) params);
        if (result.size() == 1) {
            return convertRsToObj(result.get(0), entityClazz);
        } else if (result.isEmpty()) {
            return null;
        } else {
            throw new IncorrectResultSizeDataAccessException(1);
        }
    }

    @Override
    public int deleteById(Object id, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return delete(DewSB.inst()
                .eq(entityClassInfo.pkFieldNameOpt.get(), id), entityClazz);
    }

    @Override
    public int deleteByCode(String code, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return delete(DewSB.inst()
                .eq(entityClassInfo.codeFieldNameOpt.get(), code), entityClazz);
    }

    @Override
    public int deleteAll(Class<?> entityClazz) {
        return delete(DewSB.inst(), entityClazz);
    }

    @Override
    public int delete(SB sqlBuilder, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        Object[] sb = ((DewSB) sqlBuilder).build(entityClassInfo, leftDecorated, rightDecorated);
        return jdbcTemplate.update(String.format("DELETE FROM " + leftDecorated + "%s" + rightDecorated + " %s",
                entityClassInfo.tableName, sb[0]),
                ((List) sb[1]).toArray());
    }

    @Override
    public int enableById(Object id, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return enable(DewSB.inst()
                .eq(entityClassInfo.pkFieldNameOpt.get(), id), entityClazz);
    }

    @Override
    public int enableByCode(String code, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return enable(DewSB.inst().eq(entityClassInfo.codeFieldNameOpt.get(), code), entityClazz);
    }

    @Override
    public int enable(SB sqlBuilder, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        Object[] sb = ((DewSB) sqlBuilder).build(entityClassInfo, leftDecorated, rightDecorated);
        EntityContainer.EntityClassInfo.Column column = entityClassInfo.columns.get(entityClassInfo.enabledFieldNameOpt.get());
        ((List) sb[1]).add(0, !column.reverse);
        return jdbcTemplate.update(String.format("UPDATE %s SET " + leftDecorated + "%s" + rightDecorated + " = ? %s",
                entityClassInfo.tableName,
                column.columnName,
                sb[0]),
                ((List) sb[1]).toArray());
    }

    @Override
    public int disableById(Object id, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return disable(DewSB.inst()
                .eq(entityClassInfo.pkFieldNameOpt.get(), id), entityClazz);
    }

    @Override
    public int disableByCode(String code, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return disable(DewSB.inst()
                .eq(entityClassInfo.codeFieldNameOpt.get(), code), entityClazz);
    }

    @Override
    public int disable(SB sqlBuilder, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        Object[] sb = ((DewSB) sqlBuilder).build(entityClassInfo, leftDecorated, rightDecorated);
        EntityContainer.EntityClassInfo.Column column = entityClassInfo.columns.get(entityClassInfo.enabledFieldNameOpt.get());
        ((List) sb[1]).add(0, column.reverse);
        return jdbcTemplate.update(String.format("UPDATE %s SET " + leftDecorated + "%s" + rightDecorated + " = ? %s",
                entityClassInfo.tableName,
                column.columnName,
                sb[0]),
                ((List) sb[1]).toArray());
    }

    @Override
    public int update(String sql, Object[] params) {
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public boolean existById(Object id, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return exist(DewSB.inst()
                .eq(entityClassInfo.pkFieldNameOpt.get(), id), entityClazz);
    }

    @Override
    public boolean existByCode(String code, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return exist(DewSB.inst()
                .eq(entityClassInfo.codeFieldNameOpt.get(), code), entityClazz);
    }

    @Override
    public boolean exist(SB sqlBuilder, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        Object[] sb = ((DewSB) sqlBuilder).build(entityClassInfo, leftDecorated, rightDecorated);
        return exist(String.format("SELECT 1 FROM " + leftDecorated + "%s" + rightDecorated + " %s",
                entityClassInfo.tableName,
                sb[0]),
                ((List) sb[1]).toArray());
    }

    @Override
    public boolean exist(String sql, Object[] params) {
        return jdbcTemplate.queryForObject(dialect.count(sql), params, Long.class) != 0;
    }

    @Override
    public <E> List<E> findAll(Class<E> entityClazz) {
        return find(DewSB.inst(), entityClazz);
    }

    @Override
    public <E> List<E> findEnabled(Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return find(DewSB.inst().eq(entityClassInfo.enabledFieldNameOpt.get(), true), entityClazz);
    }

    @Override
    public <E> List<E> findDisabled(Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return find(DewSB.inst().eq(entityClassInfo.enabledFieldNameOpt.get(), false), entityClazz);
    }

    @Override
    public <E> List<E> find(SB sqlBuilder, Class<E> entityClazz) {
        Object[] packageSelect = packageSelect(entityClazz, sqlBuilder);
        return find((String) packageSelect[0], (Object[]) packageSelect[1], entityClazz);
    }

    @Override
    public <E> List<E> find(String sql, Object[] params, Class<E> entityClazz) {
        return jdbcTemplate.queryForList(sql, params).stream()
                .map(row -> convertRsToObj(row, entityClazz))
                .collect(Collectors.toList());
    }

    @Override
    public long countAll(Class<?> entityClazz) {
        return count(DewSB.inst(), entityClazz);
    }

    @Override
    public long countEnabled(Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        EntityContainer.EntityClassInfo.Column column = entityClassInfo.columns.get(entityClassInfo.enabledFieldNameOpt.get());
        return count(DewSB.inst()
                .eq(entityClassInfo.enabledFieldNameOpt.get(), !column.reverse), entityClazz);
    }

    @Override
    public long countDisabled(Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        EntityContainer.EntityClassInfo.Column column = entityClassInfo.columns.get(entityClassInfo.enabledFieldNameOpt.get());
        return count(DewSB.inst()
                .eq(entityClassInfo.enabledFieldNameOpt.get(), column.reverse), entityClazz);
    }

    @Override
    public long count(SB sqlBuilder, Class<?> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        Object[] sb = ((DewSB) sqlBuilder).build(entityClassInfo, leftDecorated, rightDecorated);
        return count(String.format("SELECT 1 FROM " + leftDecorated + "%s" + rightDecorated + " %s",
                entityClassInfo.tableName,
                sb[0]),
                ((List) sb[1]).toArray());
    }

    @Override
    public long count(String sql, Object[] params) {
        return jdbcTemplate.queryForObject(dialect.count(sql), params, Long.class);
    }

    @Override
    public <E> Page<E> paging(long pageNumber, int pageSize, Class<E> entityClazz) {
        return paging(DewSB.inst(), pageNumber, pageSize, entityClazz);
    }

    @Override
    public <E> Page<E> pagingEnabled(long pageNumber, int pageSize, Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return paging(DewSB.inst()
                .eq(entityClassInfo.enabledFieldNameOpt.get(), true), pageNumber, pageSize, entityClazz);
    }

    @Override
    public <E> Page<E> pagingDisabled(long pageNumber, int pageSize, Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        return paging(DewSB.inst()
                .eq(entityClassInfo.enabledFieldNameOpt.get(), false), pageNumber, pageSize, entityClazz);
    }

    @Override
    public <E> Page<E> paging(SB sqlBuilder, long pageNumber, int pageSize, Class<E> entityClazz) {
        Object[] packageSelect = packageSelect(entityClazz, sqlBuilder);
        return paging((String) packageSelect[0], (Object[]) packageSelect[1], pageNumber, pageSize, entityClazz);
    }

    @Override
    public <E> Page<E> paging(String sql, Object[] params, long pageNumber, int pageSize, Class<E> entityClazz) {
        String countSql = wrapCountSql(sql);
        String pagedSql = wrapPagingSql(sql, pageNumber, pageSize);
        long totalRecords = jdbcTemplate.queryForObject(countSql, params, Long.class);
        List<E> objects = jdbcTemplate.queryForList(pagedSql, params).stream()
                .map(row -> convertRsToObj(row, entityClazz))
                .collect(Collectors.toList());
        return Page.build(pageNumber, pageSize, totalRecords, objects);
    }

    public String wrapPagingSql(String oriSql, long pageNumber, int pageSize) {
        return dialect.paging(oriSql, pageNumber, pageSize);
    }

    public String wrapCountSql(String oriSql) {
        return dialect.count(oriSql);
    }

    /**
     * 组装插入SQL
     *
     * @param entities 实体集合
     * @return 格式 Object[]{Sql:String,params:List<Object[]>,id:UUID}
     */
    private Object[] packageInsert(Iterable<?> entities, boolean ignoreNullValue) {
        if (!entities.iterator().hasNext()) {
            throw Dew.E.e(StandardCode.BAD_REQUEST.toString(), new RuntimeException("Entity List is empty."));
        }
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entities.iterator().next().getClass());
        String sql = null;
        Optional<String> lastIdOpt = Optional.empty();
        List<Object[]> params = new ArrayList<>();
        for (Object entity : entities) {

            Map<String, Object> values = $.bean.findValues(entity, null, null, entityClassInfo.columns.keySet(), null);
            if (entityClassInfo.pkFieldNameOpt.isPresent() && entityClassInfo.pkUUIDOpt.get()) {
                if (!values.containsKey(entityClassInfo.pkFieldNameOpt.get()) ||
                        values.get(entityClassInfo.pkFieldNameOpt.get()) == null ||
                        values.get(entityClassInfo.pkFieldNameOpt.get()).toString().isEmpty()) {
                    lastIdOpt = Optional.of($.field.createUUID());
                    values.put(entityClassInfo.pkFieldNameOpt.get(), lastIdOpt.get());
                } else {
                    lastIdOpt = Optional.of(values.get(entityClassInfo.pkFieldNameOpt.get()).toString());
                }
            } else if (entityClassInfo.pkFieldNameOpt.isPresent() &&
                    values.containsKey(entityClassInfo.pkFieldNameOpt.get())) {
                Object id = values.get(entityClassInfo.pkFieldNameOpt.get());
                if (id == null || id instanceof Number && ((Number) id).longValue() == 0) {
                    // Remove private key field
                    values.remove(entityClassInfo.pkFieldNameOpt.get());
                }
            }
            // Add ext values
            if (entityClassInfo.codeFieldNameOpt.isPresent() && entityClassInfo.codeUUIDOpt.get() &&
                    (!values.containsKey(entityClassInfo.codeFieldNameOpt.get()) ||
                            values.get(entityClassInfo.codeFieldNameOpt.get()) == null ||
                            values.get(entityClassInfo.codeFieldNameOpt.get()).toString().isEmpty())) {
                values.put(entityClassInfo.codeFieldNameOpt.get(), $.field.createUUID());
            }
            LocalDateTime now = LocalDateTime.now();
            if (entityClassInfo.createUserFieldNameOpt.isPresent()) {
                if (Dew.context().optInfo().isPresent()) {
                    values.put(entityClassInfo.createUserFieldNameOpt.get(), Dew.context().optInfo().get().getAccountCode());
                } else {
                    values.put(entityClassInfo.createUserFieldNameOpt.get(), EMPTY);
                }
            }
            if (entityClassInfo.createTimeFieldNameOpt.isPresent()) {
                values.put(entityClassInfo.createTimeFieldNameOpt.get(), now);
            }
            if (entityClassInfo.updateUserFieldNameOpt.isPresent()) {
                if (Dew.context().optInfo().isPresent()) {
                    values.put(entityClassInfo.updateUserFieldNameOpt.get(), Dew.context().optInfo().get().getAccountCode());
                } else {
                    values.put(entityClassInfo.updateUserFieldNameOpt.get(), EMPTY);
                }
            }
            if (entityClassInfo.updateTimeFieldNameOpt.isPresent()) {
                values.put(entityClassInfo.updateTimeFieldNameOpt.get(), now);
            }
            // Check null
            if (values.entrySet().stream()
                    .anyMatch(entry -> entityClassInfo.columns.get(entry.getKey()).notNull && entry.getValue() == null)) {
                throw Dew.E.e(StandardCode.BAD_REQUEST.toString(), new RuntimeException("Not Null check fail."));
            }
            // Filter null value if ignoreNullValue=true
            if (ignoreNullValue) {
                values = values.entrySet().stream()
                        .filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
            if (sql == null) {
                // Package
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO ").append(leftDecorated + entityClassInfo.tableName + rightDecorated);
                sb.append(values.entrySet().stream()
                        .map(entry -> leftDecorated + entityClassInfo.columns.get(entry.getKey()).columnName + rightDecorated)
                        .collect(Collectors.joining(", ", " (", ") ")));
                sb.append("VALUES");
                sb.append(values.keySet().stream().map(o -> "?").collect(Collectors.joining(", ", " (", ") ")));
                sql = sb.toString();
            }
            params.add(values.values().toArray());
        }
        return new Object[]{sql, params, lastIdOpt};
    }

    /**
     * 根据主键或Code组装更新SQL
     * 存在主键值时使用主键，否则使用Code，都不存在时报错
     *
     * @param entity          实体
     * @param ignoreNullValue 是否忽略null值插入
     * @return 格式 Object[]{Sql:String,params:Object[]}
     */
    private Object[] packageUpdate(Object entity, boolean ignoreNullValue) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entity.getClass());
        Map<String, Object> values = $.bean.findValues(entity, null, null, entityClassInfo.columns.keySet(), null);
        // Check id or code Not empty
        if (!entityClassInfo.pkFieldNameOpt.isPresent() && !entityClassInfo.codeFieldNameOpt.isPresent()) {
            throw Dew.E.e(StandardCode.NOT_FOUND.toString(), new RuntimeException("Need @PkColumn or @CodeColumn field."));
        }
        String whereColumnName = null;
        Object whereValue = null;
        if (entityClassInfo.pkFieldNameOpt.isPresent()
                && values.get(entityClassInfo.pkFieldNameOpt.get()) != null
                && !values.get(entityClassInfo.pkFieldNameOpt.get()).toString().isEmpty()) {
            if (!"0".equals(values.get(entityClassInfo.pkFieldNameOpt.get()).toString())) {
                whereColumnName = entityClassInfo.columns.get(entityClassInfo.pkFieldNameOpt.get()).columnName;
                whereValue = values.get(entityClassInfo.pkFieldNameOpt.get());
            }
            values.remove(entityClassInfo.pkFieldNameOpt.get());
        }
        if (whereColumnName == null) {
            if (entityClassInfo.codeFieldNameOpt.isPresent()
                    && values.get(entityClassInfo.codeFieldNameOpt.get()) != null
                    && !values.get(entityClassInfo.codeFieldNameOpt.get()).toString().isEmpty()) {
                whereColumnName = entityClassInfo.columns.get(entityClassInfo.codeFieldNameOpt.get()).columnName;
                whereValue = values.get(entityClassInfo.codeFieldNameOpt.get());
                values.remove(entityClassInfo.codeFieldNameOpt.get());
            } else {
                throw Dew.E.e(StandardCode.BAD_REQUEST.toString(), new RuntimeException("Need Private Key or Code value."));
            }
        }
        // Filter null value if ignoreNullValue=true
        if (ignoreNullValue) {
            values = values.entrySet().stream()
                    .filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        // Add ext values
        if (entityClassInfo.updateUserFieldNameOpt.isPresent()) {
            if (Dew.context().optInfo().isPresent()) {
                values.put(entityClassInfo.updateUserFieldNameOpt.get(), Dew.context().optInfo().get().getAccountCode());
            } else {
                values.put(entityClassInfo.updateUserFieldNameOpt.get(), EMPTY);
            }
        }
        if (entityClassInfo.createUserFieldNameOpt.isPresent()) {
            values.remove(entityClassInfo.createUserFieldNameOpt.get());
        }
        if (entityClassInfo.updateTimeFieldNameOpt.isPresent()) {
            values.put(entityClassInfo.updateTimeFieldNameOpt.get(), LocalDateTime.now());
        }
        if (entityClassInfo.createTimeFieldNameOpt.isPresent()) {
            values.remove(entityClassInfo.createTimeFieldNameOpt.get());
        }
        // Check null
        if (values.entrySet().stream()
                .anyMatch(entry -> entityClassInfo.columns.get(entry.getKey()).notNull && entry.getValue() == null)) {
            throw Dew.E.e(StandardCode.BAD_REQUEST.toString(), new RuntimeException("Not Null check fail."));
        }
        // Package
        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sb.append("UPDATE ").append(leftDecorated + entityClassInfo.tableName + rightDecorated).append(" SET ");
        sb.append(values.entrySet().stream()
                .map(entry -> {
                    params.add(entry.getValue());
                    return leftDecorated + entityClassInfo.columns.get(entry.getKey()).columnName + "` = ?";
                })
                .collect(Collectors.joining(", ")));
        sb.append(String.format(" WHERE " + leftDecorated + "%s" + rightDecorated + " = ?", whereColumnName));
        params.add(whereValue);
        return new Object[]{sb.toString(), params.toArray()};
    }

    /**
     * 组装查询SQL
     *
     * @param entityClazz 实体类型
     * @param sqlBuilder  SQL构造器
     * @return 格式 Object[]{Sql:String,params:Object[]}
     */
    private Object[] packageSelect(Class<?> entityClazz, SB sqlBuilder) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        StringBuilder sql = new StringBuilder();
        Object[] params = new Object[]{};
        sql.append("SELECT ");
        sql.append(entityClassInfo.columns.values().stream()
                .map(col -> leftDecorated + col.columnName + rightDecorated).collect(Collectors.joining(", ")));
        sql.append(" FROM ").append(leftDecorated + entityClassInfo.tableName + rightDecorated);
        if (sqlBuilder != null) {
            Object[] sb = ((DewSB) sqlBuilder).build(entityClassInfo, leftDecorated, rightDecorated);
            sql.append(sb[0]);
            params = ((List) sb[1]).toArray();
        }
        return new Object[]{sql.toString(), params};
    }

    /**
     * 将ResultSet转成对象
     *
     * @param rs          ResultSet(Map格式)
     * @param entityClazz 对象类型
     * @return 转换后的对象
     */
    public <E> E convertRsToObj(Map<String, Object> rs, Class<E> entityClazz) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(entityClazz);
        try {
            if ((entityClazz == String.class
                    || entityClazz == Integer.class
                    || entityClazz == int.class
                    || entityClazz == long.class
                    || entityClazz == Long.class
                    || entityClazz == float.class
                    || entityClazz == Float.class
                    || entityClazz == double.class
                    || entityClazz == Double.class
                    || entityClazz == short.class
                    || entityClazz == Short.class
                    || entityClazz == char.class
                    || entityClazz == Character.class) && rs.size() == 1) {
                return (E) rs.values().iterator().next();
            }
            E entity = entityClazz.newInstance();
            if (entityClassInfo == null) {
                Set<String> fields = $.bean.getFields(entityClazz).keySet();
                for (Map.Entry<String, Object> entry : rs.entrySet()) {
                    String key = underlineToCamel(entry.getKey().toLowerCase());
                    if (fields.contains(key)) {
                        Object r = convertRsToObject(entry);
                        $.bean.setValue(entity, key, r);
                    }
                }
            } else {
                for (Map.Entry<String, Object> entry : rs.entrySet()) {
                    if (entityClassInfo.columnRel.containsKey(entry.getKey().toLowerCase())) {
                        Object r = convertRsToObject(entry);
                        $.bean.setValue(entity, entityClassInfo.columnRel.get(entry.getKey().toLowerCase()), r);
                    }
                }
            }
            return entity;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | IllegalArgumentException e) {
            logger.error("Convert ResultSet to Object error", e);
            return null;
        }
    }

    private Object convertRsToObject(Map.Entry<String, Object> entry) {
        Object r;
        if (entry.getValue() instanceof Timestamp) {
            r = ((Timestamp) entry.getValue()).toLocalDateTime();
        } else if (entry.getValue() instanceof java.sql.Date) {
            r = ((java.sql.Date) entry.getValue()).toLocalDate();
        } else if (entry.getValue() instanceof Time) {
            r = ((Time) entry.getValue()).toLocalTime();
        } else {
            r = entry.getValue();
        }
        return r;
    }

    public <E> List<E> selectForList(Class<E> entityClazz, Map<String, Object> params, String sql) {
        Object[] result = packageSelect(sql, params, dialect.getDialectType());
        List<Map<String, Object>> list = jdbcTemplate.queryForList((String) result[0], (Object[]) result[1]);
        return entityClazz.isAssignableFrom(Map.class) ? (List<E>) list : list.stream().map(row -> convertRsToObj(row, entityClazz))
                .collect(Collectors.toList());
    }

    public <E> Page<E> selectForPaging(Class<E> entityClazz, MethodConstruction method, String sql) {
        Object[] result = packageSelect(sql, method.getParamsMap(), dialect.getDialectType());
        String countSql = wrapCountSql((String) result[0]);
        String pagedSql = wrapPagingSql((String) result[0], method.getPageNumber(), method.getPageSize());
        long totalRecords = jdbcTemplate.queryForObject(countSql, (Object[]) result[1], Long.class);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(pagedSql, (Object[]) result[1]);
        List<E> objects = entityClazz.isAssignableFrom(Map.class) ? (List<E>) list : list.stream().map(row -> convertRsToObj(row, entityClazz))
                .collect(Collectors.toList());
        return Page.build(method.getPageNumber(), method.getPageSize(), totalRecords, objects);
    }

    public Object[] packageSelect(String sql, Map<String, Object> params, DialectType dialectType) {
        Matcher m = FIELD_PLACE_HOLDER_PATTERN.matcher(sql);
        List<String> matchRegexList = new ArrayList<>();
        //将#{...}抠出来
        while (m.find()) {
            matchRegexList.add(m.group());
        }
        List<Object> list = new ArrayList<>();
        //将值不为空的key用?替换
        for (String key : matchRegexList) {
            // #{key},去掉#{}和空格,获取真实的key
            key = key.substring(2, key.length() - 1).replace(" ", EMPTY);
            Object v = params.get(key);
            if (v != null) {
                sql = sql.replaceFirst("\\#\\{\\s*" + key + "\\s*\\}", "?");
                list.add(v);
            }
        }
        SQLSelectStatement statement;
        switch (dialectType) {
            case H2:
            case MYSQL:
                statement = (SQLSelectStatement) new MySqlStatementParser(sql).parseSelect();
                break;
            case ORACLE:
                statement = (SQLSelectStatement) new OracleStatementParser(sql).parseStatement();
                break;
            case POSTGRE:
                statement = (SQLSelectStatement) new PGSQLStatementParser(sql).parseStatement();
                break;
            case SQLSERVER:
                statement = (SQLSelectStatement) new SQLServerStatementParser(sql).parseStatement();
                break;
            case DB2:
                statement = (SQLSelectStatement) new DB2StatementParser(sql).parseStatement();
                break;
            case PHOENIX:
                statement = (SQLSelectStatement) new PhoenixStatementParser(sql).parseStatement();
                break;
            default:
                statement = (SQLSelectStatement) new SQLStatementParser(sql).parseStatementList().get(0);
        }
        if (sql.contains("#{")) {
            SQLExpr sqlExpr = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getWhere();
            formatWhere(sqlExpr);
        }
        if (sql.contains(STAR)) {
            SQLTableSource sqlTableSource = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getFrom();
            List<SQLSelectItem> selectList = ((SQLSelectQueryBlock) statement.getSelect().getQuery()).getSelectList();
            List<SQLSelectItem> addList = new ArrayList<>();
            formatFrom(sqlTableSource, selectList, addList);
            selectList.addAll(addList);
        }
        sql = statement.toString();
        return new Object[]{sql, list.toArray()};
    }

    /**
     * 格式化select中的 * 为对应table 字段
     */
    private void formatFrom(SQLTableSource sqlTableSource, List<SQLSelectItem> selectList, List<SQLSelectItem> addList) {
        if (sqlTableSource == null) {
            return;
        }
        if (sqlTableSource instanceof SQLExprTableSource) {
            doFormat((SQLExprTableSource) sqlTableSource, selectList, addList);
        }
        if (sqlTableSource instanceof SQLJoinTableSource) {
            formatFrom(((SQLJoinTableSource) sqlTableSource).getRight(), selectList, addList);
            formatFrom(((SQLJoinTableSource) sqlTableSource).getLeft(), selectList, addList);
        }
    }

    private void doFormat(SQLExprTableSource sqlTableSource, List<SQLSelectItem> selectList, List<SQLSelectItem> addList) {
        EntityContainer.EntityClassInfo entityClassInfo = EntityContainer.getEntityClassByClazz(((SQLIdentifierExpr) sqlTableSource.getExpr()).getName());
        if (entityClassInfo == null) {
            return;
        }
        Iterator<SQLSelectItem> iterator = selectList.iterator();
        while (iterator.hasNext()) {
            SQLSelectItem sqlSelectItem = iterator.next();
            if (sqlSelectItem.getExpr() instanceof SQLPropertyExpr) {
                SQLPropertyExpr expr = (SQLPropertyExpr) sqlSelectItem.getExpr();
                SQLIdentifierExpr exprOwner = (SQLIdentifierExpr) expr.getOwner();
                if ((exprOwner.getName() + POINT + expr.getName()).equals(sqlTableSource.getAlias() + POINT + STAR)) {
                    iterator.remove();
                    entityClassInfo.columns.forEach((filedName, column) -> addWhenAlias(addList, exprOwner, column));
                }
            } else if (sqlSelectItem.getExpr() instanceof SQLObjectImpl) {
                iterator.remove();
                entityClassInfo.columns.forEach((filedName, column) -> addList.add(new SQLSelectItem(new SQLIdentifierExpr(leftDecorated + column.columnName + rightDecorated))));
            }
        }
    }

    private void addWhenAlias(List<SQLSelectItem> addList, SQLIdentifierExpr exprOwner, EntityContainer.EntityClassInfo.Column column) {
        if (column.columnName.equals("id") || column.columnName.equals("created_by") || column.columnName.equals("updated_by") || column.columnName.equals("created_time") ||
                column.columnName.equals("updated_time"))
            return;
        addList.add(new SQLSelectItem(new SQLPropertyExpr(exprOwner.getName(), leftDecorated + column.columnName + rightDecorated)));
    }


    private static String underlineToCamel(String param) {
        if (param == null || EMPTY.equals(param.trim())) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void formatWhere(SQLExpr sqlExpr) {
        if (sqlExpr == null) {
            return;
        }
        if (sqlExpr instanceof SQLBetweenExpr
                || sqlExpr instanceof SQLInListExpr
                || ((SQLBinaryOpExpr) sqlExpr).getLeft() instanceof SQLIdentifierExpr
                || ((SQLBinaryOpExpr) sqlExpr).getLeft() instanceof SQLPropertyExpr) {
            doFormatWhere(sqlExpr);
        } else {
            formatWhere(((SQLBinaryOpExpr) sqlExpr).getRight());
            formatWhere(((SQLBinaryOpExpr) sqlExpr).getLeft());
        }
    }

    private static void doFormatWhere(SQLExpr sqlExpr) {
        String itemStr;
        if (sqlExpr instanceof SQLBetweenExpr) {
            itemStr = ((SQLBetweenExpr) sqlExpr).getBeginExpr().toString() + ((SQLBetweenExpr) sqlExpr).getEndExpr().toString();
        } else if (sqlExpr instanceof SQLInListExpr) {
            itemStr = ((SQLInListExpr) sqlExpr).getTargetList().toString();
        } else {
            itemStr = sqlExpr.toString();
        }
        if (FIELD_PLACE_HOLDER_PATTERN.matcher(itemStr).find()) {
            if (sqlExpr.getParent() instanceof SQLBinaryOpExpr) {
                ((SQLBinaryOpExpr) sqlExpr.getParent()).replace(sqlExpr, null);
            } else if (sqlExpr.getParent() instanceof SQLSelectQueryBlock) {
                ((SQLSelectQueryBlock) sqlExpr.getParent()).replace(sqlExpr, null);
            }
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }


}

