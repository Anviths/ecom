package com.ecom.exception;

public class AccountLockedException extends ApiException{
    public AccountLockedException(String message) {
        super(message);
    }
}
