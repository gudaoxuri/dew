package group.idealworld.dew.devops.kernel.exception;

/**
 * Global process exception.
 *
 * @author gudaoxuri
 */
public class GlobalProcessException extends RuntimeException {

    /**
     * Instantiates a new Process exception.
     *
     * @param message the message
     */
    public GlobalProcessException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Process exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GlobalProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
