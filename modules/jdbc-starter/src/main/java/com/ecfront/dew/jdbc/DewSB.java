package com.ecfront.dew.jdbc;

import com.ecfront.dew.jdbc.entity.EntityContainer;
import com.ecfront.dew.core.jdbc.SB;
import com.ecfront.dew.jdbc.entity.EntityContainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DewSB implements SB {

    private List<SQLCondition> conditions = new ArrayList<>();
    private Map<String, Boolean> order = new LinkedHashMap<>();
    private static final String AND = " AND ";

    private DewSB() {
    }

    public static DewSB inst() {
        return new DewSB();
    }

    public Object[] build(Class entityClazz, String leftDecorated, String rightDecorated) {
        return build(EntityContainer.getEntityClassByClazz(entityClazz), leftDecorated, rightDecorated);
    }

    public Object[] build(EntityContainer.EntityClassInfo classInfo, String leftDecorated, String rightDecorated) {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        conditions.forEach(cond -> {
            String f = leftDecorated + classInfo.columns.get(cond.field).columnName + rightDecorated;
            switch (cond.op) {
                case EQUAL:
                    sb.append(AND + f + " = ?");
                    parameters.add(cond.value1);
                    break;
                case NOT_EQUAL:
                    sb.append(AND + f + " != ?");
                    parameters.add(cond.value1);
                    break;
                case GT:
                    sb.append(AND + f + " > ?");
                    parameters.add(cond.value1);
                    break;
                case GE:
                    sb.append(AND + f + " >= ?");
                    parameters.add(cond.value1);
                    break;
                case LT:
                    sb.append(AND + f + " < ?");
                    parameters.add(cond.value1);
                    break;
                case LE:
                    sb.append(AND+ f + " <= ?");
                    parameters.add(cond.value1);
                    break;
                case LIKE:
                    sb.append(AND + f + " LIKE ?");
                    parameters.add(cond.value1);
                    break;
                case IN:
                    sb.append(AND + f + " IN " + ((List) cond.value1).stream()
                            .map(o -> "?")
                            .collect(Collectors.joining(",", "(", ")")));
                    parameters.add(cond.value1);
                    break;
                case NOT_IN:
                    sb.append(AND + f + " NOT IN " + ((List) cond.value1).stream()
                            .map(o -> "?")
                            .collect(Collectors.joining(",", "(", ")")));
                    parameters.add(cond.value1);
                    break;
                case IS_NULL:
                    sb.append(AND + f + " IS NULL");
                    break;
                case NOT_NULL:
                    sb.append(AND + f + " IS NOT NULL");
                    break;
                case BETWEEN:
                    sb.append(AND + f + " BETWEEN ? AND ?");
                    parameters.add(cond.value1);
                    parameters.add(cond.value2);
                    break;
            }
        });
        if (!order.isEmpty()) {
            sb.append(" ORDER BY " + order.entrySet().stream()
                    .map(entry -> leftDecorated + classInfo.columns.get(entry.getKey()).columnName + rightDecorated + " " + (entry.getValue() ? "ASC" : "DESC"))
                    .collect(Collectors.joining(", ", " ", " ")));
        }
        return new Object[]{sb.toString(), parameters};
    }

    @Override
    public DewSB eq(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.EQUAL, field, value, null));
        return this;
    }

    @Override
    public DewSB notEq(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.NOT_EQUAL, field, value, null));
        return this;
    }

    @Override
    public DewSB gt(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.GT, field, value, null));
        return this;
    }

    @Override
    public DewSB ge(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.GE, field, value, null));
        return this;
    }

    @Override
    public DewSB lt(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.LT, field, value, null));
        return this;
    }

    @Override
    public DewSB le(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.LE, field, value, null));
        return this;
    }

    @Override
    public DewSB like(String field, Object value) {
        conditions.add(new SQLCondition(SQLCondition.OP.LIKE, field, value, null));
        return this;
    }

    @Override
    public DewSB in(String field, List<Object> values) {
        conditions.add(new SQLCondition(SQLCondition.OP.IN, field, values, null));
        return this;
    }

    @Override
    public DewSB notIn(String field, List<Object> values) {
        conditions.add(new SQLCondition(SQLCondition.OP.NOT_IN, field, values, null));
        return this;
    }

    @Override
    public DewSB isNull(String field) {
        conditions.add(new SQLCondition(SQLCondition.OP.IS_NULL, field, null, null));
        return this;
    }

    @Override
    public DewSB notNull(String field) {
        conditions.add(new SQLCondition(SQLCondition.OP.NOT_NULL, field, null, null));
        return this;
    }

    @Override
    public DewSB between(String field, Object value1, Object value2) {
        conditions.add(new SQLCondition(SQLCondition.OP.BETWEEN, field, value1, value2));
        return this;
    }

    @Override
    public DewSB asc(String filed) {
        order.put(filed, true);
        return this;
    }

    @Override
    public DewSB desc(String filed) {
        order.put(filed, false);
        return this;
    }

    private static class SQLCondition {

        private SQLCondition.OP op;
        private String field;
        private Object value1;
        private Object value2;

        SQLCondition(SQLCondition.OP op, String field, Object value1, Object value2) {
            this.op = op;
            this.field = field;
            this.value1 = value1;
            this.value2 = value2;
        }

        enum OP {
            EQUAL, NOT_EQUAL, GT, GE, LT, LE, IN, NOT_IN, LIKE, IS_NULL, NOT_NULL, BETWEEN
        }
    }
}
