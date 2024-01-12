package group.idealworld.dew.devops.kernel.exception;

/**
 * Config exception.
 *
 * @author gudaoxuri
 */
public class ConfigException extends RuntimeException {

    /**
     * Instantiates a new Config exception.
     *
     * @param message the message
     */
    public ConfigException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Config exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
