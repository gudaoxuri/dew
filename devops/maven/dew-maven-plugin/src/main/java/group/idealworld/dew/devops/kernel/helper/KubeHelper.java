package group.idealworld.dew.devops.kernel.helper;

import org.slf4j.Logger;

/**
 * Kubernetes操作函数类.
 *
 * @author gudaoxuri
 * @see <a href="https://github.com/kubernetes-client/java">Kubernetes
 *      Client</a>
 */
public class KubeHelper extends MultiInstProcessor {

    /**
     * Init.
     *
     * @param instanceId       the instance id
     * @param log              the log
     * @param base64KubeConfig the base 64 kube config
     */
    public static void init(String instanceId, Logger log, String base64KubeConfig) {
        multiInit("KUBE", instanceId,
                () -> new KubeOpt(log, base64KubeConfig), base64KubeConfig);
    }

    /**
     * Fetch KubeOpt instance.
     *
     * @param instanceId the instance id
     * @return KubeOpt instance
     */
    public static KubeOpt inst(String instanceId) {
        return multiInst("KUBE", instanceId);
    }

}
