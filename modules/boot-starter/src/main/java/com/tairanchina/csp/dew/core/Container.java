package com.tairanchina.csp.dew.core;

import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

public class Container {
    public static final Map<Class, Class<?>> DAO_CONTAINER = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);
    public static final Map<Class, Class<?>> SERVICE_ENTITY_CONTAINER = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);
    public static final Map<Class, Object> SERVICE_DAO_BEAN_CONTAINER = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);
    public static final Map<Class, Object> CONTROLLER_SERVICE_BEAN_CONTAINER = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);

}
