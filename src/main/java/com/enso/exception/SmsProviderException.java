package com.enso.exception;

public class SmsProviderException extends RuntimeException {

    public SmsProviderException(String message,Throwable cause) {
        super(message,cause);
    }
}
