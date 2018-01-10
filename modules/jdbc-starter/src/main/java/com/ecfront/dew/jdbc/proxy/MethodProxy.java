package com.ecfront.dew.jdbc.proxy;

import com.ecfront.dew.Dew;
import com.ecfront.dew.jdbc.annotations.Select;
import com.ecfront.dew.Dew;
import com.ecfront.dew.jdbc.DewDS;
import com.ecfront.dew.jdbc.DewDao;
import com.ecfront.dew.jdbc.annotations.Select;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


public class MethodProxy implements InvocationHandler {

    private static final Map<Method, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentReferenceHashMap<>(10, ConcurrentReferenceHashMap.ReferenceType.SOFT);

    private static final Map<Class, String> REL_DAO_DS = new ConcurrentReferenceHashMap<>(10, ConcurrentReferenceHashMap.ReferenceType.SOFT);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String dsName = getRelDAOAndDS(method.getDeclaringClass(), proxy);
        if (!method.getDeclaringClass().isInterface() && !method.isDefault()) {
            // This proxy is class and method has not impl
            return method.invoke(this, args);
        } else if (method.isDefault()) {
            // This proxy is interface but method has impl
            return getMethodHandle(method).bindTo(proxy)
                    .invokeWithArguments(args);
        } else {
            // This proxy is interface
            return run(method, args, dsName);
        }
    }

    private String getRelDAOAndDS(Class clazz, Object proxy) throws Throwable {
        if (clazz == DewDao.class) {
            return "";
        }
        String dsName = REL_DAO_DS.getOrDefault(clazz, null);
        if (dsName != null) {
            return dsName;
        }
        Method dsMethod = clazz.getMethod("ds");
        if (!clazz.isInterface() && !dsMethod.isDefault()) {
            // This proxy is class and method has not impl
            dsName = (String) dsMethod.invoke(this);
        } else if (dsMethod.isDefault()) {
            // This proxy is interface but method has impl
            dsName = (String) getMethodHandle(dsMethod).bindTo(proxy).invokeWithArguments();
        }
        REL_DAO_DS.put(clazz, dsName);
        return dsName;
    }


    private MethodHandle getMethodHandle(Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        MethodHandle handle = METHOD_HANDLE_CACHE.get(method);
        if (handle == null) {
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            final Class<?> declaringClass = method.getDeclaringClass();
            handle = constructor.newInstance(declaringClass)
                    .unreflectSpecial(method, declaringClass);
            METHOD_HANDLE_CACHE.put(method, handle);
        }
        return handle;
    }

    /**
     * 实现接口的核心方法
     */
    public Object run(Method m, Object[] args, String dsName) {
        MethodConstruction method = new MethodConstruction(m, args);
        for (Annotation annotation : method.getMethodAnnotations()) {
            if (annotation instanceof Select) {
                if (method.flagOfPaging()) {
                    return ((DewDS) Dew.ds(dsName)).selectForPaging(((Select) annotation).entityClass(), method, ((Select) annotation).value());
                }
                List list = ((DewDS) Dew.ds(dsName)).selectForList(((Select) annotation).entityClass(), method.getParamsMap(), ((Select) annotation).value());
                if (!method.getReturnType().isAssignableFrom(List.class)) {
                    return !list.isEmpty() ? list.get(0) : null;
                }
                return list;
            }
        }
        return null;
    }

}
