package group.idealworld.dew.devops.kernel.resource;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

/**
 * The interface Kubernetes resource builder.
 *
 * @param <T> the type parameter
 * @author gudaoxuri
 */
public interface KubeResourceBuilder<T> {

    /**
     * Build.
     *
     * @param config the project config
     * @return the result
     */
    T build(FinalProjectConfig config);

}
