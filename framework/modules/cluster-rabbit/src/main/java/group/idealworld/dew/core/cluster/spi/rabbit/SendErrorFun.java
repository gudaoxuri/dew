package group.idealworld.dew.core.cluster.spi.rabbit;

/**
 * The interface Send error fun.
 *
 * @author gudaoxuri
 */
@FunctionalInterface
public interface SendErrorFun {

    /**
     * Invoke.
     *
     * @param ex           the ex
     * @param beforeResult the before result
     */
    void invoke(Exception ex, Object beforeResult);

}
