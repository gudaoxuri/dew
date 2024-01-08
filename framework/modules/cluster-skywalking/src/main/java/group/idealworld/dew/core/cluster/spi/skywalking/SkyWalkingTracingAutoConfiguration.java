package group.idealworld.dew.core.cluster.spi.skywalking;

import group.idealworld.dew.core.cluster.spi.skywalking.config.TraceInterceptorConfigurer;
import group.idealworld.dew.core.cluster.ClusterTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Created on 2022/9/25.
 *
 * @author 迹_Jason
 */
@Slf4j
@Configuration
public class SkyWalkingTracingAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    public ClusterTrace skyWalkingClusterTrace() {
        return new SkyWalkingClusterTrace();
    }

    @Bean
    public TraceInterceptorConfigurer traceInterceptorConfigurer(ClusterTrace clusterTrace) {
        return new TraceInterceptorConfigurer(clusterTrace);
    }

}
