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

    default P insert(Object entity) {
        return (P)Dew.ds(ds()).insert(entity);
    }

    default void insert(Iterable<?> entities) {
        Dew.ds(ds()).insert(entities);
    }

    default void updateById(P id, Object entity) {
        Dew.ds(ds()).updateById(id, entity);
    }

    default void updateByCode(String code, Object entity) {
        Dew.ds(ds()).updateByCode(code, entity);
    }

    default E getById(P id) {
        return Dew.ds(ds()).getById(id, getClazz());
    }

    default E getByCode(String code) {
        return Dew.ds(ds()).getByCode(code, getClazz());
    }

    default void deleteById(P id) {
        Dew.ds(ds()).deleteById(id, getClazz());
    }

    default void deleteByCode(String code) {
        Dew.ds(ds()).deleteByCode(code, getClazz());
    }

    default void enableById(P id) {
        Dew.ds(ds()).enableById(id, getClazz());
    }

    default void enableByCode(String code) {
        Dew.ds(ds()).enableByCode(code, getClazz());
    }

    default void disableById(P id) {
        Dew.ds(ds()).disableById(id, getClazz());
    }

    default void disableByCode(String code) {
        Dew.ds(ds()).disableByCode(code, getClazz());
    }

    default boolean existById(P id) {
        return Dew.ds(ds()).existById(id, getClazz());
    }

    default boolean existByCode(String code) {
        return Dew.ds(ds()).existByCode(code, getClazz());
    }

    default List<E> findAll() {
        return findAll(null);
    }

    default List<E> findAll(LinkedHashMap<String, Boolean> orderDesc) {
        return Dew.ds(ds()).findAll(orderDesc, getClazz());
    }

    default List<E> findEnabled() {
        return findEnabled(null);
    }

    default List<E> findEnabled(LinkedHashMap<String, Boolean> orderDesc) {
        return Dew.ds(ds()).findEnabled(orderDesc, getClazz());
    }

    default List<E> findDisabled() {
        return findDisabled(null);
    }

    default List<E> findDisabled(LinkedHashMap<String, Boolean> orderDesc) {
        return Dew.ds(ds()).findDisabled(orderDesc, getClazz());
    }

    default long countAll() {
        return Dew.ds(ds()).countAll(getClazz());
    }

    default long countEnabled() {
        return Dew.ds(ds()).countEnabled(getClazz());
    }

    default long countDisabled() {
        return Dew.ds(ds()).countDisabled(getClazz());
    }

    default Page<E> paging(long pageNumber, int pageSize) {
        return paging(pageNumber, pageSize, null);
    }

    default Page<E> paging(long pageNumber, int pageSize, LinkedHashMap<String, Boolean> orderDesc) {
        return Dew.ds(ds()).paging(pageNumber, pageSize, orderDesc, getClazz());
    }

    default Page<E> pagingEnabled(long pageNumber, int pageSize) {
        return pagingEnabled(pageNumber, pageSize, null);
    }

    default Page<E> pagingEnabled(long pageNumber, int pageSize, LinkedHashMap<String, Boolean> orderDesc) {
        return Dew.ds(ds()).pagingEnabled(pageNumber, pageSize, orderDesc, getClazz());
    }

    default Page<E> pagingDisabled(long pageNumber, int pageSize) {
        return pagingDisabled(pageNumber, pageSize, null);
    }

    default Page<E> pagingDisabled(long pageNumber, int pageSize, LinkedHashMap<String, Boolean> orderDesc) {
        return Dew.ds(ds()).pagingDisabled(pageNumber, pageSize, orderDesc, getClazz());
    }

}

