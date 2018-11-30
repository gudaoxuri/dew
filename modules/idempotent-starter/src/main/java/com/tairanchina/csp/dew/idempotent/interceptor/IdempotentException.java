package com.tairanchina.csp.dew.idempotent.interceptor;

public class IdempotentException extends RuntimeException {

    public IdempotentException(String message) {
        super(message);
    }
}
