package com.tairanchina.csp.dew.core;

import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

public class Container {

    public static final Map<Class, Class<?>> DAO_CONTAINER = new ConcurrentReferenceHashMap<>(50, ConcurrentReferenceHashMap.ReferenceType.SOFT);

}
