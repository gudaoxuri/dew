package group.idealworld.dew.devops.kernel.helper;

/**
 * Kubernetes Resource enumeration.
 *
 * @author gudaoxuri
 */
public enum KubeRES {
    /**
     * Name space kube res.
     */
    NAME_SPACE("Namespace"),
    /**
     * Ingress kube res.
     */
    INGRESS("Ingress"),
    /**
     * Service kube res.
     */
    SERVICE("Service"),
    /**
     * Service account kube res.
     */
    SERVICE_ACCOUNT("ServiceAccount"),
    /**
     * Deployment kube res.
     */
    DEPLOYMENT("Deployment"),
    /**
     * Daemon set kube res.
     */
    DAEMON_SET("DaemonSet"),
    /**
     * Replica set kube res.
     */
    REPLICA_SET("ReplicaSet"),
    /**
     * Config map kube res.
     */
    CONFIG_MAP("ConfigMap"),
    /**
     * Secret kube res.
     */
    SECRET("Secret"),
    /**
     * Pod kube res.
     */
    POD("Pod"),
    /**
     * Role kube res.
     */
    ROLE("Role"),
    /**
     * Rule binding kube res.
     */
    ROLE_BINDING("RoleBinding"),
    /**
     * Cluster role kube res.
     */
    CLUSTER_ROLE("ClusterRole"),
    /**
     * Cluster rule binding kube res.
     */
    CLUSTER_ROLE_BINDING("ClusterRoleBinding"),
    /**
     * Horizontal pod autoscaler kube res.
     */
    HORIZONTAL_POD_AUTOSCALER("HorizontalPodAutoscaler");

    /**
     * Value.
     */
    String val;

    KubeRES(String val) {
        this.val = val;
    }

    /**
     * Parse kube res.
     *
     * @param val the val
     * @return the kube res
     */
    public static KubeRES parse(String val) {
        for (KubeRES res : KubeRES.values()) {
            if (res.val.equalsIgnoreCase(val)) {
                return res;
            }
        }
        return null;
    }

    /**
     * Gets value.
     *
     * @return value
     */
    public String getVal() {
        return val;
    }
}
