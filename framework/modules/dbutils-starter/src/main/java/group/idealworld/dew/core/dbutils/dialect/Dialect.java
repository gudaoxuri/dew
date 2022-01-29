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

public interface Dialect {

    String paging(String sql, long pageNumber, long pageSize) throws SQLException;

    String count(String sql) throws SQLException;

    String getTableInfo(String tableName) throws SQLException;

    @Deprecated
    String createTableIfNotExist(String tableName, String tableDesc, Map<String, String> fields, Map<String, String> fieldsDesc,
                                 List<String> indexFields, List<String> uniqueFields, String pkField) throws SQLException;

    String validationQuery();

    String getDriver();

    DialectType getDialectType();
}
