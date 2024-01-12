package group.idealworld.dew.devops.kernel.resource;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

import java.util.Map;

/**
 * Kubernetes config map builder.
 *
 * @author gudaoxuri
 */
public class KubeConfigMapBuilder implements KubeResourceBuilder<V1ConfigMap> {

    @Override
    public V1ConfigMap build(FinalProjectConfig config) {
        return null;
    }

    /**
     * Build config map.
     *
     * @param name      the name
     * @param namespace the namespace
     * @param labels    the labels
     * @param data      the data
     * @return the config map
     */
    public V1ConfigMap build(String name, String namespace, Map<String, String> labels, Map<String, String> data) {
        return new V1ConfigMap()
                .kind(KubeRES.CONFIG_MAP.getVal())
                .apiVersion("v1")
                .data(data)
                .metadata(new V1ObjectMeta()
                        .name(name)
                        .namespace(namespace)
                        .labels(labels));
    }

}
