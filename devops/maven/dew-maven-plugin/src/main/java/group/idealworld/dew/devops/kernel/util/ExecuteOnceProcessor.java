package group.idealworld.dew.devops.kernel.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 只执行一次的处理器.
 *
 * @author gudaoxuri
 */
public class ExecuteOnceProcessor {

    private static final Map<String, AtomicBoolean> initialized = new ConcurrentHashMap<>();

    public static synchronized boolean executedCheck(Class clazz) {
        initialized.putIfAbsent(clazz.getName(), new AtomicBoolean());
        return initialized.get(clazz.getName()).getAndSet(true);
    }

}
