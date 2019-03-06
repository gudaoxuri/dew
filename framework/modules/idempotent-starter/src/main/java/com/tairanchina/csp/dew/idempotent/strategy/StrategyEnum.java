package com.tairanchina.csp.dew.idempotent.strategy;

public enum StrategyEnum {

    ITEM("item"), BLOOM_FLTER("bloom"), AUTO("auto");

    private String value;

    StrategyEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
