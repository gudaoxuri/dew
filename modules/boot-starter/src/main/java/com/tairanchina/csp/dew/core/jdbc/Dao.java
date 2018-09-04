package com.tairanchina.csp.dew.core.jdbc;

import com.ecfront.dew.common.Page;

import java.util.List;

public interface Dao<P, E> {

    String ds();

    DS getDS();

    P insert(Object entity);

    void insert(Iterable<?> entities);

    int updateById(P id, Object entity);

    int updateByCode(String code, Object entity);

    E getById(P id);

    E getByCode(String code);

    E get(SB sqlBuilder);

    int deleteById(P id);

    int deleteByCode(String code);

    int delete(SB sqlBuilder);

    int enableById(P id);

    int enableByCode(String code);

    int enable(SB sqlBuilder);

    int disableById(P id);

    int disableByCode(String code);

    int disable(SB sqlBuilder);

    boolean existById(P id);

    boolean existByCode(String code);

    boolean exist(SB sqlBuilder);

    List<E> findAll();

    List<E> findEnabled();

    List<E> findDisabled();

    List<E> find(SB sqlBuilder);

    List<E> find(String sql, Object[] params);

    long countAll();

    long countEnabled();

    long countDisabled();

    long count(SB sqlBuilder);

    Page<E> paging(long pageNumber, int pageSize);

    Page<E> pagingEnabled(long pageNumber, int pageSize);

    Page<E> pagingDisabled(long pageNumber, int pageSize);

    Page<E> paging(SB sqlBuilder, long pageNumber, int pageSize);

    Page<E> paging(String sql, Object[] params, long pageNumber, int pageSize);
}
