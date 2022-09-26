package groop.idealworld.dew.core.cluster.spi.skywalking;

import group.idealworld.dew.core.cluster.ClusterTrace;

/**
 * Created on 2022/9/25.
 *
 * @author 迹_Jason
 */

public class SkyWalkingClusterTrace implements ClusterTrace {
    private final static ThreadLocal<String> TRACE_ID_STORAGE = new ThreadLocal<>();

    @Override
    public void setTraceId(String traceId) {
        TRACE_ID_STORAGE.set(traceId);
    }

    @Override
    public void removeTraceId() {
        TRACE_ID_STORAGE.remove();
    }

    @Override
    public String getTraceId() {
        return TRACE_ID_STORAGE.get();
    }
}
