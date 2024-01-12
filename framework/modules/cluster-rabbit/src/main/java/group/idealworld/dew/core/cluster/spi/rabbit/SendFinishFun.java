package group.idealworld.dew.core.cluster.spi.rabbit;

/**
 * The interface Send finish fun.
 *
 * @author gudaoxuri
 */
@FunctionalInterface
public interface SendFinishFun {

    /**
     * Invoke.
     *
     * @param beforeResult the before result
     */
    void invoke(Object beforeResult);

}
