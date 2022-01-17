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

package group.idealworld.dew.core.dbutils.dialect;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class PostgresDialect implements Dialect {

    @Override
    public String paging(String sql, long pageNumber, long pageSize) throws SQLException {
        return sql + " LIMIT " + pageSize + " OFFSET " + (pageNumber - 1) * pageSize;
    }

    @Override
    public String count(String sql) throws SQLException {
        return "SELECT COUNT(1) FROM ( " + sql + " ) _" + System.currentTimeMillis();
    }

    @Override
    public String getTableInfo(String tableName) throws SQLException {
        return "SELECT * FROM pg_tables t WHERE t.tablename = '" + tableName + "'";
    }

    @Override
    public String createTableIfNotExist(String tableName, String tableDesc, Map<String, String> fields, Map<String, String> fieldsDesc, List<String> indexFields, List<String> uniqueFields, String pkField) throws SQLException {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " ( ");
        for (Map.Entry<String, String> field : fields.entrySet()) {
            String f = field.getValue().toLowerCase();
            String t;
            switch (f) {
                case "seq":
                    t = "serial";
                    break;
                case "int":
                case "integer":
                case "short":
                    t = "integer";
                    break;
                case "long":
                    t = "bigint";
                    break;
                case "string":
                    t = "character varying(65535)";
                    break;
                case "text":
                    t = "text";
                    break;
                case "bool":
                case "boolean":
                    t = "boolean";
                    break;
                case "float":
                case "double":
                    t = "double precision";
                    break;
                case "char":
                    t = "character";
                    break;
                case "date":
                    t = "date";
                    break;
                case "bigdecimal":
                case "decimal":
                    t = "numeric";
                    break;
                default:
                    throw new SQLException("Not support type:" + f);
            }
            sb.append(field.getKey()).append(" ").append(t).append(" ,");
        }
        if (uniqueFields != null && !uniqueFields.isEmpty()) {
            for (String uField : uniqueFields) {
                sb.append("CONSTRAINT \"u_").append(tableName).append("_").append(uField).append("\" UNIQUE (\"").append(uField).append("\"),");
            }
        }
        if (pkField != null && !Objects.equals(pkField.trim(), "")) {
            sb.append("primary key(").append(pkField.trim()).append(") );");
        } else {
            sb = new StringBuilder(sb.substring(0, sb.length() - 1) + ");");
        }
        if (indexFields != null && !indexFields.isEmpty()) {
            for (String idxFields : indexFields) {
                sb.append("CREATE INDEX \"i_").append(tableName).append("_").append(idxFields).append("\" ON \"").append(tableName).append("\" (\"").append(idxFields).append("\");");
            }
        }
        if (tableDesc != null && !tableDesc.isEmpty()) {
            sb.append("COMMENT ON TABLE \"").append(tableName).append("\" IS '").append(tableDesc).append("';");
        }
        if (fieldsDesc != null && !fieldsDesc.isEmpty()) {
            for (Map.Entry<String, String> field : fieldsDesc.entrySet()) {
                sb.append("COMMENT ON COLUMN \"").append(tableName).append("\".\"").append(field.getKey()).append("\" IS '").append(field.getValue()).append("';");
            }
        }
        return sb.toString();
    }

    @Override
    public String validationQuery() {
        return "SELECT 'x'";
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
