package group.idealworld.dew.core.cluster.spi.rabbit;

/**
 * The interface Receive finish fun.
 *
 * @author gudaoxuri
 */
@FunctionalInterface
public interface ReceiveFinishFun {

    /**
     * Invoke.
     *
     * @param beforeResult the before result
     */
    void invoke(Object beforeResult);

}
