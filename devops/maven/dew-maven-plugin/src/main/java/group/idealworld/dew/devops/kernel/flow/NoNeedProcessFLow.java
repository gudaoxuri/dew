package group.idealworld.dew.devops.kernel.flow;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;

/**
 * 不需要执行的缺省流程定义.
 *
 * @author gudaoxuri
 */
public class NoNeedProcessFLow extends BasicFlow {
    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {

    }
}
