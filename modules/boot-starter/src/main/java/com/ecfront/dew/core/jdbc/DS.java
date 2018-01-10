package com.ecfront.dew.core.jdbc;

import com.ecfront.dew.common.Page;

import java.util.List;

public interface DS {

    Object insert(Object entity);

    void insert(Iterable<?> entities);

    int updateById(Object id, Object entity);

    int updateByCode(String code, Object entity);

    <E> E getById(Object id, Class<E> entityClazz);

    <E> E getByCode(String code, Class<E> entityClazz);

    <E> E get(SB sqlBuilder, Class<E> entityClazz);

    <E> E get(String sql, Object[] params, Class<E> entityClazz);

    int deleteById(Object id, Class<?> entityClazz);

    int deleteByCode(String code, Class<?> entityClazz);

    int deleteAll(Class<?> entityClazz);

    int delete(SB sqlBuilder, Class<?> entityClazz);

    int enableById(Object id, Class<?> entityClazz);

    int enableByCode(String code, Class<?> entityClazz);

    int enable(SB sqlBuilder, Class<?> entityClazz);

    int disableById(Object id, Class<?> entityClazz);

    int disableByCode(String code, Class<?> entityClazz);

    int disable(SB sqlBuilder, Class<?> entityClazz);

    int update(String sql, Object[] params);

    boolean existById(Object id, Class<?> entityClazz);

    boolean existByCode(String code, Class<?> entityClazz);

    boolean exist(SB sqlBuilder, Class<?> entityClazz);

    boolean exist(String sql, Object[] params);

    <E> List<E> findAll(Class<E> entityClazz);

    <E> List<E> findEnabled(Class<E> entityClazz);

    <E> List<E> findDisabled(Class<E> entityClazz);

    <E> List<E> find(SB sqlBuilder, Class<E> entityClazz);

    <E> List<E> find(String sql, Object[] params, Class<E> entityClazz);

    long countAll(Class<?> entityClazz);

    long countEnabled(Class<?> entityClazz);

    long countDisabled(Class<?> entityClazz);

    long count(SB sqlBuilder, Class<?> entityClazz);

    long count(String sql, Object[] params);

    <E> Page<E> paging(long pageNumber, int pageSize, Class<E> entityClazz);

    <E> Page<E> pagingEnabled(long pageNumber, int pageSize, Class<E> entityClazz);

    <E> Page<E> pagingDisabled(long pageNumber, int pageSize, Class<E> entityClazz);

    <E> Page<E> paging(SB sqlBuilder, long pageNumber, int pageSize, Class<E> entityClazz);

    <E> Page<E> paging(String sql, Object[] params, long pageNumber, int pageSize, Class<E> entityClazz);
}
