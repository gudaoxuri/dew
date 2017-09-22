package com.ecfront.dew.core.test.web;

public class AuthException extends RuntimeException {

    private String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }

}
