package group.idealworld.dew.devops.kernel.helper;

/**
 * Kubernetes basic res.
 * <p>
 * 用于提取各资源公共字段
 *
 * @author gudaoxuri
 */
public class KubeBasicRes {
    private String apiVersion = null;
    private String kind = null;
    private KubeBasicMetadataMeta metadata = null;

    /**
     * Gets api version.
     *
     * @return the api version
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Sets api version.
     *
     * @param apiVersion the api version
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets kind.
     *
     * @param kind the kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public KubeBasicMetadataMeta getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(KubeBasicMetadataMeta metadata) {
        this.metadata = metadata;
    }
}
