package com.ecfront.dew.idempotent.strategy;

public enum StatusEnum {

    NOT_EXIST("NOT_EXIST"), UN_CONFIRM("UN_CONFIRM"), CONFIRMED("CONFIRMED");

    private String value;

    StatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
