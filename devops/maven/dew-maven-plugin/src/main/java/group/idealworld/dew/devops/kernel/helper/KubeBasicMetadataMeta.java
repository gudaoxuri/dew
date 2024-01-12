package group.idealworld.dew.devops.kernel.helper;

/**
 * Kubernetes basic metadata meta.
 * <p>
 * 用于提取各资源公共字段
 *
 * @author gudaoxuri
 */
public class KubeBasicMetadataMeta {
    private String name = null;
    private String namespace = null;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets namespace.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets namespace.
     *
     * @param namespace the namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
