package group.idealworld.dew.devops.kernel.exception;

/**
 * Git diff exception.
 *
 * @author gudaoxuri
 */
public class GitDiffException extends RuntimeException {

    /**
     * Instantiates a new git diff exception.
     *
     * @param message the message
     */
    public GitDiffException(String message) {
        super(message);
    }

    /**
     * Instantiates a new git diff exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GitDiffException(String message, Throwable cause) {
        super(message, cause);
    }
}
