package com.ecom.dto;

import com.ecom.exception.ApiException;

public class PasswordReuseException extends ApiException {
    public PasswordReuseException(String message) {
        super(message);
    }
}
