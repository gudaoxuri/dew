package group.idealworld.dew.core.basic.utils;


import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.basic.resp.StandardResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yiye
 * @date 2022/3/29
 * @description 生成获取保存traceId
 **/
public class TraceIdUtil {
    private static final Logger logger = LoggerFactory.getLogger(TraceIdUtil.class);
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

    public static String createResponseCode(String code,String businessFlag){
        var trace = Dew.Info.name + businessFlag + ":" + TraceIdUtil.getTraceId();
        logger.trace("TRACE:[{}] {}", businessFlag, trace);
        return code+"-"+ $.security.digest.digest(trace, "MD5");
    }
}
