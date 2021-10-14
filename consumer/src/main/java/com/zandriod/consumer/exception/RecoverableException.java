package com.zandriod.consumer.exception;

public class RecoverableException extends RuntimeException {

    private static final long serialVersionUID = 4931706434060642843L;


    public RecoverableException(String message) {
        super(message);
    }

    public RecoverableException(String message, Throwable ex) {
        super(message, ex);
    }
}