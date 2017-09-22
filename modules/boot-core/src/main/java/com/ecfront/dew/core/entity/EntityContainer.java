package com.ecfront.dew.core.entity;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.BeanHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnClass({JdbcTemplate.class})
public class EntityContainer {

    private static final Map<String, EntityClassInfo> COLUMN_INFO = new ConcurrentHashMap<>();

    private static void loadEntityClassInfo(Class clazz) {
        Map<String, BeanHelper.FieldInfo> fieldInfo = $.bean.findFieldsInfo(
                clazz, null, null, null, new HashSet<Class<? extends Annotation>>() {{
                    add(PkColumn.class);
                    add(CodeColumn.class);
                    add(CreateUserColumn.class);
                    add(CreateTimeColumn.class);
                    add(UpdateUserColumn.class);
                    add(UpdateTimeColumn.class);
                    add(EnabledColumn.class);
                    add(Column.class);
                }});
        if (!fieldInfo.isEmpty()) {
            EntityClassInfo entityClassInfo = new EntityClassInfo();
            // table info
            Entity entity = (Entity) Arrays.stream(clazz.getAnnotations()).filter(ann -> ann.annotationType() == Entity.class).findAny().get();
            if (entity.tableName().isEmpty()) {
                entityClassInfo.tableName = camelToUnderline(clazz.getSimpleName()).toLowerCase();
            } else {
                entityClassInfo.tableName = entity.tableName().toLowerCase();
            }
            // column info
            fieldInfo.values().forEach(field -> {
                boolean isContinue = false;
                if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == PkColumn.class)) {
                    // private key column
                    PkColumn pkColumn = (PkColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == PkColumn.class).findAny().get();
                    entityClassInfo.pkFieldNameOpt = Optional.of(field.getName());
                    entityClassInfo.pkUUIDOpt = Optional.of(pkColumn.uuid());
                    entityClassInfo.columns.put(field.getName(),
                            EntityClassInfo.Column.build(pkColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : pkColumn.columnName(), true));
                    isContinue = true;
                }
                if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == CodeColumn.class)) {
                    // code column
                    CodeColumn codeColumn = (CodeColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == CodeColumn.class).findAny().get();
                    entityClassInfo.codeFieldNameOpt = Optional.of(field.getName());
                    entityClassInfo.codeUUIDOpt = Optional.of(codeColumn.uuid());
                    entityClassInfo.columns.put(field.getName(),
                            EntityClassInfo.Column.build(codeColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : codeColumn.columnName(), true));
                    isContinue = true;
                }
                if (!isContinue) {
                    if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == CreateUserColumn.class)) {
                        // create user column
                        CreateUserColumn createUserColumn = (CreateUserColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == CreateUserColumn.class).findAny().get();
                        entityClassInfo.createUserFieldNameOpt = Optional.of(field.getName());
                        entityClassInfo.columns.put(field.getName(),
                                EntityClassInfo.Column.build(createUserColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : createUserColumn.columnName(), false));
                        isContinue = true;
                    }
                    if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == UpdateUserColumn.class)) {
                        // update user column
                        UpdateUserColumn updateUserColumn = (UpdateUserColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == UpdateUserColumn.class).findAny().get();
                        entityClassInfo.updateUserFieldNameOpt = Optional.of(field.getName());
                        entityClassInfo.columns.put(field.getName(),
                                EntityClassInfo.Column.build(updateUserColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : updateUserColumn.columnName(), false));
                        isContinue = true;
                    }
                    if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == CreateTimeColumn.class)) {
                        // create time column
                        CreateTimeColumn createTimeColumn = (CreateTimeColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == CreateTimeColumn.class).findAny().get();
                        entityClassInfo.createTimeFieldNameOpt = Optional.of(field.getName());
                        entityClassInfo.columns.put(field.getName(),
                                EntityClassInfo.Column.build(createTimeColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : createTimeColumn.columnName(), false));
                        isContinue = true;
                    }
                    if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == UpdateTimeColumn.class)) {
                        // update time column
                        UpdateTimeColumn updateTimeColumn = (UpdateTimeColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == UpdateTimeColumn.class).findAny().get();
                        entityClassInfo.updateTimeFieldNameOpt = Optional.of(field.getName());
                        entityClassInfo.columns.put(field.getName(),
                                EntityClassInfo.Column.build(updateTimeColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : updateTimeColumn.columnName(), false));
                        isContinue = true;
                    }
                    if (field.getAnnotations().stream().anyMatch(ann -> ann.annotationType() == EnabledColumn.class)) {
                        // enabled column
                        EnabledColumn enabledColumn = (EnabledColumn) field.getAnnotations().stream().filter(ann -> ann.annotationType() == EnabledColumn.class).findAny().get();
                        entityClassInfo.enabledFieldNameOpt = Optional.of(field.getName());
                        entityClassInfo.columns.put(field.getName(),
                                EntityClassInfo.Column.build(enabledColumn.columnName().isEmpty() ? camelToUnderline(field.getName()) : enabledColumn.columnName(),
                                        false,
                                        enabledColumn.reverse()));
                        isContinue = true;
                    }
                    if (!isContinue) {
                        Column column = (Column) field.getAnnotations().stream().filter(ann -> ann.annotationType() == Column.class).findAny().get();
                        entityClassInfo.columns.put(field.getName(),
                                EntityClassInfo.Column.build(column.columnName().isEmpty() ? camelToUnderline(field.getName()) : column.columnName(), column.notNull()));
                    }
                }
            });
            entityClassInfo.columns.forEach((k, v) -> entityClassInfo.columnRel.put(v.columnName, k));
            COLUMN_INFO.put(clazz.getName(), entityClassInfo);
        }
    }

    public static EntityClassInfo getEntityClassByClazz(Class<?> clazz) {
        if (!COLUMN_INFO.containsKey(clazz.getName())) {
            loadEntityClassInfo(clazz);
        }
        return COLUMN_INFO.get(clazz.getName());
    }

    public static EntityClassInfo getEntityClassByClazz(String tableName) {
        for (Map.Entry<String, EntityClassInfo> entry : COLUMN_INFO.entrySet()) {
            if (entry.getValue().tableName.equals(tableName)) {
                return entry.getValue();
            }
        }
        return null;
    }


    public static String camelToUnderline(String name) {
        StringBuilder sb = new StringBuilder();
        if (name != null && name.length() > 0) {
            sb.append(name.substring(0, 1).toUpperCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    sb.append("_");
                }
                sb.append(s.toUpperCase());
            }
        }
        return sb.toString();
    }

    public static class EntityClassInfo {
        public String tableName;

        public Optional<String> pkFieldNameOpt = Optional.empty();
        public Optional<Boolean> pkUUIDOpt = Optional.empty();

        public Optional<String> codeFieldNameOpt = Optional.empty();
        public Optional<Boolean> codeUUIDOpt = Optional.empty();

        public Optional<String> createUserFieldNameOpt = Optional.empty();
        public Optional<String> createTimeFieldNameOpt = Optional.empty();
        public Optional<String> updateUserFieldNameOpt = Optional.empty();
        public Optional<String> updateTimeFieldNameOpt = Optional.empty();

        public Optional<String> enabledFieldNameOpt = Optional.empty();

        // fieldName -> Column
        public Map<String, Column> columns = new HashMap<>();

        // columnName -> fieldName
        public Map<String, String> columnRel = new HashMap<>();

        public static class Column {
            public String columnName;
            public boolean notNull;
            public boolean reverse;

            public static Column build(String columnName, boolean notNull) {
                return build(columnName, notNull, false);
            }

            public static Column build(String columnName, boolean notNull, boolean reverse) {
                Column column = new Column();
                column.columnName = columnName.toLowerCase();
                column.notNull = notNull;
                column.reverse = reverse;
                return column;
            }
        }
    }

}
