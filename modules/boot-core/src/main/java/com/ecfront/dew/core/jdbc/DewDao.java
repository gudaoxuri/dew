package com.ecfront.dew.core.jdbc;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.core.Container;
import com.ecfront.dew.core.Dew;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;

public interface DewDao<P,E> {

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
    
    default DS getDS(){
       return getDS();
    }

    default P insert(Object entity) {
        return (P)getDS().insert(entity);
    }

    default void insert(Iterable<?> entities) {
        getDS().insert(entities);
    }

    default void updateById(P id, Object entity) {
        getDS().updateById(id, entity);
    }

    default void updateByCode(String code, Object entity) {
        getDS().updateByCode(code, entity);
    }

    default E getById(P id) {
        return getDS().getById(id, getClazz());
    }

    default E getByCode(String code) {
        return getDS().getByCode(code, getClazz());
    }

    default void deleteById(P id) {
        getDS().deleteById(id, getClazz());
    }

    default void deleteByCode(String code) {
        getDS().deleteByCode(code, getClazz());
    }

    default void enableById(P id) {
        getDS().enableById(id, getClazz());
    }

    default void enableByCode(String code) {
        getDS().enableByCode(code, getClazz());
    }

    default void disableById(P id) {
        getDS().disableById(id, getClazz());
    }

    default void disableByCode(String code) {
        getDS().disableByCode(code, getClazz());
    }

    default boolean existById(P id) {
        return getDS().existById(id, getClazz());
    }

    default boolean existByCode(String code) {
        return getDS().existByCode(code, getClazz());
    }

    default List<E> findAll() {
        return findAll(null);
    }

    default List<E> findAll(LinkedHashMap<String, Boolean> orderDesc) {
        return getDS().findAll(orderDesc, getClazz());
    }

    default List<E> findEnabled() {
        return findEnabled(null);
    }

    default List<E> findEnabled(LinkedHashMap<String, Boolean> orderDesc) {
        return getDS().findEnabled(orderDesc, getClazz());
    }

    default List<E> findDisabled() {
        return findDisabled(null);
    }

    default List<E> findDisabled(LinkedHashMap<String, Boolean> orderDesc) {
        return getDS().findDisabled(orderDesc, getClazz());
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

    default Page<E> paging(long pageNumber, int pageSize) {
        return paging(pageNumber, pageSize, null);
    }

    default Page<E> paging(long pageNumber, int pageSize, LinkedHashMap<String, Boolean> orderDesc) {
        return getDS().paging(pageNumber, pageSize, orderDesc, getClazz());
    }

    default Page<E> pagingEnabled(long pageNumber, int pageSize) {
        return pagingEnabled(pageNumber, pageSize, null);
    }

    default Page<E> pagingEnabled(long pageNumber, int pageSize, LinkedHashMap<String, Boolean> orderDesc) {
        return getDS().pagingEnabled(pageNumber, pageSize, orderDesc, getClazz());
    }

    default Page<E> pagingDisabled(long pageNumber, int pageSize) {
        return pagingDisabled(pageNumber, pageSize, null);
    }

    default Page<E> pagingDisabled(long pageNumber, int pageSize, LinkedHashMap<String, Boolean> orderDesc) {
        return getDS().pagingDisabled(pageNumber, pageSize, orderDesc, getClazz());
    }

}

