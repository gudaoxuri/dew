package groop.idealworld.dew.core.cluster.spi.skywalking.interceptor;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.cluster.ClusterTrace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yiye
 **/
public class TraceInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceInterceptor.class);

    public static final String TRACE_ID_HEADER = "Trace-Id";

    private final ClusterTrace clusterTrace;

    public TraceInterceptor(ClusterTrace clusterTrace) {
        this.clusterTrace = clusterTrace;
    }

    /**
     * skyWalking 未部署情况下的错误码
     */
    private static final String ERROR_STR = "Ignored_Trace,N/A";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = response.getHeader(TRACE_ID_HEADER) != null ? response.getHeader(TRACE_ID_HEADER) : "";
        if (!StringUtils.hasLength(traceId)) {
            traceId = TraceContext.traceId();
        }
        // 未插入探针的情况 或skyWalking未运行的情况
        if (!StringUtils.hasLength(traceId) || ERROR_STR.contains(traceId)) {
            traceId = $.field.createShortUUID();
        }
        response.setHeader(TRACE_ID_HEADER, traceId);
        //放入日志MDC
        MDC.put("tid", traceId);
        //放入本地线程
        clusterTrace.setTraceId(traceId);
        LOGGER.info("Trace-Id:{},PATH:{}", traceId, request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        MDC.remove("tid");
        clusterTrace.removeTraceId();
    }
}
