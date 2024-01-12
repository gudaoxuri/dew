package group.idealworld.dew.devops.kernel.flow.scale;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.resource.KubeHorizontalPodAutoscalerBuilder;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Default scale flow.
 *
 * @author gudaoxuri
 */
public class DefaultScaleFlow extends BasicFlow {

    private int replicas;
    private boolean autoScale;
    private int minReplicas;
    private int maxReplicas;
    private int cpuAvg;

    /**
     * Instantiates a new Default scale flow.
     *
     * @param replicas    the replicas
     * @param autoScale   the auto scale
     * @param minReplicas the min replicas
     * @param maxReplicas the max replicas
     * @param cpuAvg      the cpu avg
     */
    public DefaultScaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg) {
        this.replicas = replicas;
        this.autoScale = autoScale;
        this.minReplicas = minReplicas;
        this.maxReplicas = maxReplicas;
        this.cpuAvg = cpuAvg;
    }

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        if (!autoScale) {
            logger.info("Change replicas number is " + replicas);
            KubeHelper.inst(config.getId()).patch(config.getAppName(), new ArrayList<>() {
                {
                    add("{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":" + replicas + "}");
                }
            }, config.getNamespace(), KubeRES.DEPLOYMENT);
        } else {
            logger.info("Enabled auto scale between " + minReplicas + " and " + maxReplicas);
            KubeHelper.inst(config.getId()).apply(
                    new KubeHorizontalPodAutoscalerBuilder().build(config, minReplicas, maxReplicas, cpuAvg));
        }
    }

}
