package group.idealworld.dew.idempotent.strategy;

/**
 * Status enum.
 *
 * @author gudaoxuri
 */
public enum StatusEnum {

    /**
     * Not exist status enum.
     */
    NOT_EXIST("NOT_EXIST"),
    /**
     * Un confirm status enum.
     */
    UN_CONFIRM("UN_CONFIRM"),
    /**
     * Confirmed status enum.
     */
    CONFIRMED("CONFIRMED");

    private String value;

    StatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
