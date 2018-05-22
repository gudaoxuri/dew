package com.tairanchina.csp.dew.core.jdbc;

import java.util.List;

public interface SB {

    SB eq(String field, Object value);

    SB notEq(String field, Object value);

    SB gt(String field, Object value);

    SB ge(String field, Object value);

    SB lt(String field, Object value);

    SB le(String field, Object value);

    SB like(String field, Object value);

    SB in(String field, List<Object> values);

    SB notIn(String field, List<Object> values);

    SB isNull(String field);

    SB notNull(String field);

    SB between(String field, Object value1, Object value2);

    SB asc(String filed);

    SB desc(String filed);
}
