package group.idealworld.dew.core.cluster.spi.rocket;

@FunctionalInterface
public interface ReceiveFinishFun {

    /**
     * Invoke.
     *
     * @param beforeResult the before result
     */
    void invoke(Object beforeResult);

}
