package com.ecfront.dew.core;

import java.util.Map;
import java.util.WeakHashMap;

public class Container {
    public static final Map<Class, Class<?>> DAO_CONTAINER = new WeakHashMap<>();
    public static final Map<Class, Class<?>> SERVICE_ENTITY_CONTAINER = new WeakHashMap<>();
    public static final Map<Class, Object> SERVICE_DAO_BEAN_CONTAINER = new WeakHashMap<>();
    public static final Map<Class, Object> CONTROLLER_SERVICE_BEAN_CONTAINER = new WeakHashMap<>();

}
