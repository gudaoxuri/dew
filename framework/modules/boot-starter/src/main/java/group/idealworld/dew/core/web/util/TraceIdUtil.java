package group.idealworld.dew.core.web.util;


/**
 * @author yiye
 * @date 2022/3/29
 * @description 生成获取保存traceId
 **/
public class TraceIdUtil {
    private static final ThreadLocal<String> THREAD_LOCAL_TRACE = new ThreadLocal<>();

    public static String getTraceId() {
        return THREAD_LOCAL_TRACE.get();
    }

    public static void setTraceId(String traceId) {
        THREAD_LOCAL_TRACE.set(traceId);
    }

    public static void removeTraceId() {
        THREAD_LOCAL_TRACE.remove();
    }
}
