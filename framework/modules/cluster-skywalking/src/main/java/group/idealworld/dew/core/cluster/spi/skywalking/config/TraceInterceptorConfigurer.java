package group.idealworld.dew.core.cluster.spi.skywalking.config;

import group.idealworld.dew.core.cluster.spi.skywalking.interceptor.TraceInterceptor;
import group.idealworld.dew.core.cluster.ClusterTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yiye
 **/
@Slf4j
@ConditionalOnWebApplication
public class TraceInterceptorConfigurer implements WebMvcConfigurer {

    private final ClusterTrace clusterTrace;

    public TraceInterceptorConfigurer(ClusterTrace clusterTrace) {
        this.clusterTrace = clusterTrace;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Enabled SkyWalking Tracing...");
        registry.addInterceptor(new TraceInterceptor(clusterTrace)).addPathPatterns("/**").order(1);
    }
}
