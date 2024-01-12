package group.idealworld.dew.idempotent.interceptor;

/**
 * Idempotent exception.
 *
 * @author gudaoxuri
 */
public class IdempotentException extends RuntimeException {

    /**
     * Instantiates a new Idempotent exception.
     *
     * @param message the message
     */
    public IdempotentException(String message) {
        super(message);
    }
}
