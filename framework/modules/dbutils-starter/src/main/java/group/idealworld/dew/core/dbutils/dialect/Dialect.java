package group.idealworld.dew.core.dbutils.dialect;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface Dialect {

    String paging(String sql, long pageNumber, long pageSize) throws SQLException;

    String count(String sql) throws SQLException;

    String getTableInfo(String tableName) throws SQLException;

    @Deprecated
    String createTableIfNotExist(String tableName, String tableDesc, Map<String, String> fields,
            Map<String, String> fieldsDesc, List<String> indexFields,
            List<String> uniqueFields, String pkField) throws SQLException;

    String validationQuery();

    String getDriver();

    DialectType getDialectType();
}
