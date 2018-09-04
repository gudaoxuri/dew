package com.tairanchina.csp.dew.jdbc;

import com.ecfront.dew.common.Page;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.jdbc.DS;
import com.tairanchina.csp.dew.core.jdbc.Dao;
import com.tairanchina.csp.dew.core.jdbc.SB;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public interface DewDao<P,E> extends Dao<P, E> {

    default Class<E> getClazz() {
        Class<E> clazz = (Class<E>) Container.DAO_CONTAINER.get(this.getClass());
        if (clazz != null) {
            return clazz;
        }
        if (Proxy.class.isAssignableFrom(this.getClass())) {
            clazz = (Class<E>) (((ParameterizedType) this.getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[1]);
        } else {
            clazz = (Class<E>) (((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1]);
        }
        Container.DAO_CONTAINER.put(this.getClass(), clazz);
        return clazz;
    }

    @Override
    default String ds() {
        return "";
    }

    @Override
    default DS getDS() {
        return Dew.ds(ds());
    }

    @Override
    default P insert(Object entity) {
        return (P) getDS().insert(entity);
    }

    @Override
    default void insert(Iterable<?> entities) {
        getDS().insert(entities);
    }

    @Override
    default int updateById(P id, Object entity) {
        return getDS().updateById(id, entity);
    }

    @Override
    default int updateByCode(String code, Object entity) {
        return getDS().updateByCode(code, entity);
    }

    @Override
    default E getById(P id) {
        return getDS().getById(id, getClazz());
    }

    @Override
    default E getByCode(String code) {
        return getDS().getByCode(code, getClazz());
    }

    @Override
    default E get(SB sqlBuilder) {
        return getDS().get(sqlBuilder, getClazz());
    }

    @Override
    default int deleteById(P id) {
        return getDS().deleteById(id, getClazz());
    }

    @Override
    default int deleteByCode(String code) {
        return getDS().deleteByCode(code, getClazz());
    }

    @Override
    default int delete(SB sqlBuilder) {
        return getDS().delete(sqlBuilder, getClazz());
    }

    @Override
    default int enableById(P id) {
        return getDS().enableById(id, getClazz());
    }

    @Override
    default int enableByCode(String code) {
        return getDS().enableByCode(code, getClazz());
    }

    @Override
    default int enable(SB sqlBuilder) {
        return getDS().enable(sqlBuilder, getClazz());
    }

    @Override
    default int disableById(P id) {
        return getDS().disableById(id, getClazz());
    }

    @Override
    default int disableByCode(String code) {
        return getDS().disableByCode(code, getClazz());
    }

    @Override
    default int disable(SB sqlBuilder) {
        return getDS().disable(sqlBuilder, getClazz());
    }

    @Override
    default boolean existById(P id) {
        return getDS().existById(id, getClazz());
    }

    @Override
    default boolean existByCode(String code) {
        return getDS().existByCode(code, getClazz());
    }

    @Override
    default boolean exist(SB sqlBuilder) {
        return getDS().exist(sqlBuilder, getClazz());
    }

    @Override
    default List<E> findAll() {
        return getDS().findAll(getClazz());
    }

    @Override
    default List<E> findEnabled() {
        return getDS().findEnabled(getClazz());
    }

    @Override
    default List<E> findDisabled() {
        return getDS().findDisabled(getClazz());
    }

    @Override
    default List<E> find(SB sqlBuilder) {
        return getDS().find(sqlBuilder, getClazz());
    }

    @Override
    default List<E> find(String sql, Object[] params) {
        return getDS().find(sql, params, getClazz());
    }

    @Override
    default long countAll() {
        return getDS().countAll(getClazz());
    }

    @Override
    default long countEnabled() {
        return getDS().countEnabled(getClazz());
    }

    @Override
    default long countDisabled() {
        return getDS().countDisabled(getClazz());
    }

    @Override
    default long count(SB sqlBuilder) {
        return getDS().count(sqlBuilder, getClazz());
    }

    @Override
    default Page<E> paging(long pageNumber, int pageSize) {
        return getDS().paging(pageNumber, pageSize, getClazz());
    }

    @Override
    default Page<E> pagingEnabled(long pageNumber, int pageSize) {
        return getDS().pagingEnabled(pageNumber, pageSize, getClazz());
    }

    @Override
    default Page<E> pagingDisabled(long pageNumber, int pageSize) {
        return getDS().pagingDisabled(pageNumber, pageSize, getClazz());
    }

    @Override
    default Page<E> paging(SB sqlBuilder, long pageNumber, int pageSize) {
        return getDS().paging(sqlBuilder, pageNumber, pageSize, getClazz());
    }

    @Override
    default Page<E> paging(String sql, Object[] params, long pageNumber, int pageSize) {
        return getDS().paging(sql, params, pageNumber, pageSize, getClazz());
    }

}

