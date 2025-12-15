package com.ecom.exception;

public class PasswordReuseException extends ApiException {
    public PasswordReuseException(String message) {
        super(message);
    }
}
