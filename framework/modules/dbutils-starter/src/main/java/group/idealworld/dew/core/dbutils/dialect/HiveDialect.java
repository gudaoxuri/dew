package group.idealworld.dew.core.dbutils.dialect;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

class HiveDialect implements Dialect {

    @Override
    public String paging(String sql, long pageNumber, long pageSize) throws SQLException {
        return sql + " LIMIT " + (pageNumber - 1) * pageSize + ", " + pageSize;
    }

    @Override
    public String count(String sql) throws SQLException {
        return "SELECT COUNT(1) FROM ( " + sql + " ) _" + System.currentTimeMillis();
    }

    @Override
    public String getTableInfo(String tableName) throws SQLException {
        // TODO
        throw new RuntimeException("NotImplementedException");
    }

    @Override
    public String createTableIfNotExist(String tableName, String tableDesc, Map<String, String> fields,
            Map<String, String> fieldsDesc, List<String> indexFields,
            List<String> uniqueFields, String pkField) throws SQLException {
        // TODO
        throw new RuntimeException("NotImplementedException");
        /*
         * StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS " +
         * tableName + " ( ");
         * for (Map.Entry<String, String> field : fields.entrySet()) {
         * String f = field.getValue().toLowerCase();
         * String t;
         * switch (f) {
         * case "int":
         * case "integer":
         * t = "INT";
         * break;
         * case "long":
         * t = "BIGINT";
         * break;
         * case "short":
         * t = "SMALLINT";
         * break;
         * case "string":
         * t = "STRING";
         * break;
         * case "bool":
         * case "boolean":
         * t = "BOOLEAN";
         * break;
         * case "float":
         * t = "FLOAT";
         * break;
         * case "double":
         * t = "DOUBLE";
         * break;
         * case "char":
         * t = "CHAR";
         * break;
         * case "date":
         * t = "TIMESTAMP";
         * break;
         * case "decimal":
         * t = "DECIMAL";
         * break;
         * default:
         * throw new SQLException("Not support type:" + f);
         * }
         * sb.append(field.getKey()).append(" ").append(t).append(" ,");
         * }
         * return sb.substring(0, sb.length() - 1) + ")";
         */
    }

    @Override
    public String validationQuery() {
        return "SELECT 1";
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
