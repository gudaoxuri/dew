package group.idealworld.dew.core.dbutils.dialect;

public class DialectFactory {

    private DialectFactory() {
    }

    public static Dialect parseDialect(String url) {
        if (url.startsWith("jdbc:mysql")) {
            return new MySQLDialect();
        } else if (url.startsWith("jdbc:postgresql")) {
            return new PostgresDialect();
        } else if (url.startsWith("jdbc:hive2")) {
            return new HiveDialect();
        }
        return null;
    }

}
