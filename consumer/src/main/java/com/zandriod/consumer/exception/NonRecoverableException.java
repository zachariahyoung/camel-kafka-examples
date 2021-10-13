package com.zandriod.consumer.exception;

public class NonRecoverableException extends RuntimeException {

    private static final long serialVersionUID = 4931706434060642843L;


    public NonRecoverableException(String message) {
        super(message);
    }

    public NonRecoverableException(String message, Throwable ex) {
        super(message, ex);
    }
}