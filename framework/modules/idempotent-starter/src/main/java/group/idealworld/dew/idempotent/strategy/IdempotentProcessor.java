package group.idealworld.dew.idempotent.strategy;

/**
 * The interface Idempotent processor.
 *
 * @author gudaoxuri
 */
public interface IdempotentProcessor {

    /**
     * Process status enum.
     *
     * @param optType    the opt type
     * @param optId      the opt id
     * @param initStatus the init status
     * @param expireMs   the expire ms
     * @return the status enum
     */
    StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs);

    /**
     * Confirm boolean.
     *
     * @param optType the opt type
     * @param optId   the opt id
     * @return the boolean
     */
    boolean confirm(String optType, String optId);

    /**
     * Cancel boolean.
     *
     * @param optType the opt type
     * @param optId   the opt id
     * @return the boolean
     */
    boolean cancel(String optType, String optId);

}
