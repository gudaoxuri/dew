package group.idealworld.dew.devops.kernel.flow.log;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.function.PodSelector;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.resource.KubeDeploymentBuilder;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Default log flow.
 *
 * @author gudaoxuri
 */
public class DefaultLogFlow extends BasicFlow {

    private String podName;
    private boolean follow;

    /**
     * Instantiates a new Default log flow.
     *
     * @param podName the pod name
     * @param follow  the follow
     */
    public DefaultLogFlow(String podName, boolean follow) {
        this.podName = podName;
        this.follow = follow;
    }

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        podName = PodSelector.select(config, Optional.ofNullable(podName));
        logger.info("--------- Show pod : " + podName + " logs ---------");
        if (follow) {
            new Thread(() -> {
                try {
                    KubeHelper.inst(config.getId()).log(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME,
                            config.getNamespace(),
                            System.out::println);
                } catch (ApiException e) {
                    logger.error("Log error", e);
                }
            }).start();
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        } else {
            StringBuffer sb = new StringBuffer();
            KubeHelper.inst(config.getId())
                    .log(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, config.getNamespace())
                    .forEach(lineMsg -> {
                        System.out.println(lineMsg);
                        sb.append(lineMsg).append("\r\n");
                    });
            Files.write(Paths.get(flowBasePath + "tail.log"), sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

}
