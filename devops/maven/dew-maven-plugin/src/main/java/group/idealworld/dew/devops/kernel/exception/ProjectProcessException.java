package group.idealworld.dew.devops.kernel.exception;

/**
 * Project process exception.
 *
 * @author gudaoxuri
 */
public class ProjectProcessException extends RuntimeException {

    /**
     * Instantiates a new Process exception.
     *
     * @param message the message
     */
    public ProjectProcessException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Process exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ProjectProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
