package com.ecfront.dew.core.jdbc;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.core.Container;
import com.ecfront.dew.core.Dew;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public interface DewDao<P, E> {

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

    default String ds() {
        return "";
    }

    default DS getDS() {
        return Dew.ds(ds());
    }

    default P insert(Object entity) {
        return (P) getDS().insert(entity);
    }

    default void insert(Iterable<?> entities) {
        getDS().insert(entities);
    }

    default int updateById(P id, Object entity) {
        return getDS().updateById(id, entity);
    }

    default int updateByCode(String code, Object entity) {
        return getDS().updateByCode(code, entity);
    }

    default E getById(P id) {
        return getDS().getById(id, getClazz());
    }

    default E getByCode(String code) {
        return getDS().getByCode(code, getClazz());
    }

    default E get(DS.SB sqlBuilder) {
        return getDS().get(sqlBuilder, getClazz());
    }

    default int deleteById(P id) {
        return getDS().deleteById(id, getClazz());
    }

    default int deleteByCode(String code) {
        return getDS().deleteByCode(code, getClazz());
    }

    default int delete(DS.SB sqlBuilder) {
        return getDS().delete(sqlBuilder, getClazz());
    }

    default int enableById(P id) {
        return getDS().enableById(id, getClazz());
    }

    default int enableByCode(String code) {
        return getDS().enableByCode(code, getClazz());
    }

    default int enable(DS.SB sqlBuilder) {
        return getDS().enable(sqlBuilder, getClazz());
    }

    default int disableById(P id) {
        return getDS().disableById(id, getClazz());
    }

    default int disableByCode(String code) {
        return getDS().disableByCode(code, getClazz());
    }

    default int disable(DS.SB sqlBuilder) {
        return getDS().disable(sqlBuilder, getClazz());
    }

    default boolean existById(P id) {
        return getDS().existById(id, getClazz());
    }

    default boolean existByCode(String code) {
        return getDS().existByCode(code, getClazz());
    }

    default boolean exist(DS.SB sqlBuilder) {
        return getDS().exist(sqlBuilder, getClazz());
    }

    default List<E> findAll() {
        return getDS().findAll(getClazz());
    }

    default List<E> findEnabled() {
        return getDS().findEnabled(getClazz());
    }

    default List<E> findDisabled() {
        return getDS().findDisabled(getClazz());
    }

    default List<E> find(DS.SB sqlBuilder) {
        return getDS().find(sqlBuilder, getClazz());
    }

    default List<E> find(String sql, Object[] params) {
        return getDS().find(sql, params, getClazz());
    }

    default long countAll() {
        return getDS().countAll(getClazz());
    }

    default long countEnabled() {
        return getDS().countEnabled(getClazz());
    }

    default long countDisabled() {
        return getDS().countDisabled(getClazz());
    }

    default long count(DS.SB sqlBuilder) {
        return getDS().count(sqlBuilder, getClazz());
    }

    default Page<E> paging(long pageNumber, int pageSize) {
        return getDS().paging(pageNumber, pageSize, getClazz());
    }

    default Page<E> pagingEnabled(long pageNumber, int pageSize) {
        return getDS().pagingEnabled(pageNumber, pageSize, getClazz());
    }

    default Page<E> pagingDisabled(long pageNumber, int pageSize) {
        return getDS().pagingDisabled(pageNumber, pageSize, getClazz());
    }

    default Page<E> paging(DS.SB sqlBuilder, long pageNumber, int pageSize) {
        return getDS().paging(sqlBuilder, pageNumber, pageSize, getClazz());
    }

    default Page<E> paging(String sql, Object[] params, long pageNumber, int pageSize) {
        return getDS().paging(sql, params, pageNumber, pageSize, getClazz());
    }
}

