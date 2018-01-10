package com.ecfront.dew.idempotent.interceptor;

public class DewIdempotentException extends RuntimeException {

    public DewIdempotentException(String message) {
        super(message);
    }
}
