/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
