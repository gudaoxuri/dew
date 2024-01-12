package group.idealworld.dew.core.cluster.spi.rocket;

@FunctionalInterface
public interface ReceiveErrorFun {

    /**
     * Invoke.
     *
     * @param ex           the ex
     * @param beforeResult the before result
     */
    void invoke(Exception ex, Object beforeResult);

}
