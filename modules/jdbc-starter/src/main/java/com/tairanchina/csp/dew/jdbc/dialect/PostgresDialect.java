package com.tairanchina.csp.dew.jdbc.dialect;

class PostgresDialect implements Dialect {

    @Override
    public String paging(String sql, long pageNumber, int pageSize) {
        return sql + " LIMIT " + pageSize + " OFFSET " + (pageNumber - 1) * pageSize;
    }

    @Override
    public String count(String sql) {
        int hashCode = sql.hashCode();
        if (hashCode != Integer.MIN_VALUE) {
            return "SELECT COUNT(1) FROM ( " + sql + " ) _" + (Math.abs(hashCode));
        } else {
            return "SELECT COUNT(1) FROM ( " + sql + " ) _" + (Math.abs(hashCode) & 0x7fffffff);
        }
    }

    @Override
    public String getTableInfo(String tableName) {
        return "SELECT * FROM pg_tables t WHERE t.tablename='" + tableName + "'";
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public DialectType getDialectType() {
        return DialectType.POSTGRE;
    }
}
