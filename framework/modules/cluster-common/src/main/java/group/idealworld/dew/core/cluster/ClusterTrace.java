package group.idealworld.dew.core.cluster;

/**
 * Created on 2022/9/25.
 *
 * @author 迹_Jason
 */
public interface ClusterTrace {

    default String getTraceId() {
        return "";
    }

    void setTraceId(String traceId);

    void removeTraceId();
}
