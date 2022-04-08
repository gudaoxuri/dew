package groop.idealworld.dew.skywalking.interceptor;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.web.util.TraceIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yiye
 * @date 2022/3/30
 * @description
 **/
public class TraceInterceptor implements HandlerInterceptor {


    /**
     * skyWalking 未部署情况下的错误码
     */
    private static final String ERROR_STR = "Ignored_Trace,N/A";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var traceId = TraceContext.traceId();
        // 未插入探针的情况 或skyWalking未运行的情况
        if(StringUtils.isBlank(traceId) || ERROR_STR.contains(traceId)){
            traceId = $.field.createShortUUID();
        }
        //放入头部
        request.setAttribute("traceId",traceId);
        //放入日志MDC
        MDC.put("tid", traceId);
        //放入本地线程
        TraceIdUtil.setTraceId(traceId);
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        MDC.remove("traceId");
        TraceIdUtil.removeTraceId();
    }
}
