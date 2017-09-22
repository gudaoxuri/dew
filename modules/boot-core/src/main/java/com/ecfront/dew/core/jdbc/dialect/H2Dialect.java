package com.ecfront.dew.core.jdbc.dialect;

class H2Dialect implements Dialect {

    @Override
    public String paging(String sql, long pageNumber, int pageSize) {
        return sql + " LIMIT " + pageSize + " OFFSET " + (pageNumber - 1) * pageSize;
    }

    @Override
    public String count(String sql) {
        return "SELECT COUNT(1) FROM ( " + sql + " ) ";
    }

    @Override
    public String getTableInfo(String tableName) {
        return "SELECT * FROM INFORMATION_SCHEMA.TABLES t WHERE t.table_name ='" + tableName + "'";
    }

    @Override
    public String getDriver() {
        return "org.h2.Driver";
    }

    @Override
    public DialectType getDialectType() {
        return DialectType.H2;
    }

}
