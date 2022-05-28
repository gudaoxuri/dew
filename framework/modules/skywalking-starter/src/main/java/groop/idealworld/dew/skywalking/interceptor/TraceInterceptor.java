package groop.idealworld.dew.skywalking.interceptor;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.basic.utils.TraceIdUtil;
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
    private static Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);


    /**
     * skyWalking 未部署情况下的错误码
     */
    private static final String ERROR_STR = "Ignored_Trace,N/A";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = response.getHeader("Trace-Id") != null ? response.getHeader("Trace-Id") : "";
        if (!StringUtils.hasLength(traceId)) {
            traceId = TraceIdUtil.getTraceId();
        }
        if (!StringUtils.hasLength(traceId)) {
            traceId = TraceContext.traceId();
        }
        // 未插入探针的情况 或skyWalking未运行的情况
        if (!StringUtils.hasLength(traceId) || ERROR_STR.contains(traceId)) {
            traceId = $.field.createShortUUID();
        }
        response.setHeader("Trace-Id", traceId);
        //放入日志MDC
        MDC.put("tid", traceId);
        //放入本地线程
        TraceIdUtil.setTraceId(traceId);
        logger.info("Trace-Id:{},PATH:{}", traceId, request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        if (ex != null) {
            MDC.remove("traceId");
            TraceIdUtil.removeTraceId();
        }
    }
}
