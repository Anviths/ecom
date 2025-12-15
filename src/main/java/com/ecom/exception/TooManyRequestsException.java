package com.ecom.exception;

public class TooManyRequestsException extends ApiException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
