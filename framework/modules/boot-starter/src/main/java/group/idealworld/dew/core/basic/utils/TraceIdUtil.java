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

    private TraceIdUtil() {
    }

    public static String createResponseCode(String code, String businessFlag) {
        var trace = Dew.Info.name + businessFlag + ":" + Dew.cluster.trace.getTraceId();
        LOGGER.trace("TRACE:[{}] {}", businessFlag, trace);
        return code + "-" + $.security.digest.digest(trace, "MD5");
    }
}
