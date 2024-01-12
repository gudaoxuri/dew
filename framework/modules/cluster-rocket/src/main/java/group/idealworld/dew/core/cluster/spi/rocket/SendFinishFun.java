package group.idealworld.dew.core.cluster.spi.rocket;

@FunctionalInterface
public interface SendFinishFun {

    /**
     * Invoke.
     *
     * @param beforeResult the before result
     */
    void invoke(Object beforeResult);

}
