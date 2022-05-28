package group.idealworld.dew.core.basic.utils;


import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yiye
 **/
public class TraceIdUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceIdUtil.class);
    private static final ThreadLocal<String> THREAD_LOCAL_TRACE = new ThreadLocal<>();

    private TraceIdUtil() {
    }

    public static String getTraceId() {
        return THREAD_LOCAL_TRACE.get();
    }

    public static void setTraceId(String traceId) {
        THREAD_LOCAL_TRACE.set(traceId);
    }

    public static void removeTraceId() {
        THREAD_LOCAL_TRACE.remove();
    }

    public static String createResponseCode(String code, String businessFlag) {
        var trace = Dew.Info.name + businessFlag + ":" + TraceIdUtil.getTraceId();
        LOGGER.trace("TRACE:[{}] {}", businessFlag, trace);
        return code + "-" + $.security.digest.digest(trace, "MD5");
    }
}
