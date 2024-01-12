package group.idealworld.dew.devops.kernel.helper;

import com.ecfront.dew.common.$;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 多实例处理，支持多个实例Id共享同一实例对象.
 *
 * @author gudaoxuri
 */
public abstract class MultiInstProcessor {

    /**
     * The constant EXISTS.
     */
    protected static final ConcurrentHashMap<String, String> EXISTS = new ConcurrentHashMap<>();
    /**
     * The constant INSTANCES.
     */
    protected static final Map<String, Object> INSTANCES = new HashMap<>();

    /**
     * 初始化多实例.
     * <p>
     * 实例类型+实例Id 全局唯一，通过 hashItems 区别是否共享实例对象
     *
     * @param <T>        实例Type
     * @param kind       实例类型
     * @param instanceId 实例Id
     * @param initFun    初始化方法
     * @param hashItems  相同实例判定
     */
    protected static <T> void multiInit(String kind, String instanceId, Supplier<T> initFun, String... hashItems) {
        if (INSTANCES.containsKey(kind + "-" + instanceId)) {
            return;
        }
        String hashStr = kind + String.join("-", hashItems);
        String hash = $.security.digest.digest(hashStr, "MD5");
        if (EXISTS.containsKey(hash)) {
            INSTANCES.put(kind + "-" + instanceId, INSTANCES.get(EXISTS.get(hash)));
            return;
        }
        EXISTS.put(hash, kind + "-" + instanceId);
        INSTANCES.put(kind + "-" + instanceId, initFun.get());
    }

    /**
     * 初始化多实例.
     * <p>
     * 不判断实例对象是否共享，多用于Mock场景
     *
     * @param <T>        实例Type
     * @param kind       实例类型
     * @param instanceId 实例Id
     * @param instObj    实例对象
     */
    public static <T> void forceInit(String kind, String instanceId, T instObj) {
        INSTANCES.put(kind + "-" + instanceId, instObj);
    }

    /**
     * 获取实例对象.
     *
     * @param <T>        实例Type
     * @param kind       实例类型
     * @param instanceId 实例Id
     * @return 实例对象 t
     */
    protected static <T> T multiInst(String kind, String instanceId) {
        return (T) INSTANCES.get(kind + "-" + instanceId);
    }

}
