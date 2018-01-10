package com.ecfront.dew.jdbc.dialect;


public class DialectFactory {

    public static Dialect parseDialect(String url) {
        if (url.contains("jdbc:h2:")) {
            return new H2Dialect();
        } else if (url.contains("jdbc:mysql:")) {
            return new MySQLDialect();
        } else if (url.contains("jdbc:postgresql:")) {
            return new PostgresDialect();
        } else if (url.contains("jdbc:oracle:")) {
            return new OracleDialect();
        } else if (url.contains("jdbc:hive2:")) {
            return new HiveDialect();
        }
        return null;
    }

}
