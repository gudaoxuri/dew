package com.trc.test.web;

public class AuthException extends RuntimeException {

    private String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }

}
