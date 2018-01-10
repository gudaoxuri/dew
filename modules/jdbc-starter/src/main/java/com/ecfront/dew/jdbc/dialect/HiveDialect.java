package com.ecfront.dew.jdbc.dialect;


class HiveDialect implements Dialect {

    @Override
    public String paging(String sql, long pageNumber, int pageSize) {
        return sql + " LIMIT " + (pageNumber - 1) * pageSize + ", " + pageSize;
    }

    @Override
    public String count(String sql) {
        return "SELECT COUNT(1) FROM ( " + sql + " ) _" + System.currentTimeMillis();
    }

    @Override
    public String getTableInfo(String tableName) {
        //TODO
        throw new RuntimeException("NotImplemented");
    }

    @Override
    public String getDriver() {
        return "org.apache.hive.jdbc.HiveDriver";
    }

    @Override
    public DialectType getDialectType() {
        return DialectType.HIVE;
    }
}
