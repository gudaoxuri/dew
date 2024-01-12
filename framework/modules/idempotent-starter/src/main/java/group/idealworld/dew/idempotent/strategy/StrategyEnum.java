package group.idealworld.dew.idempotent.strategy;

/**
 * Strategy enum.
 *
 * @author gudaoxuri
 */
public enum StrategyEnum {

    /**
     * Item strategy enum.
     */
    ITEM("item"),
    /**
     * Bloom flter strategy enum.
     */
    BLOOM_FLTER("bloom"),
    /**
     * Auto strategy enum.
     */
    AUTO("auto");

    private String value;

    StrategyEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
