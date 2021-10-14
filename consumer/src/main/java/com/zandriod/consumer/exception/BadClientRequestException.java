package com.zandriod.consumer.exception;

public class BadClientRequestException extends RuntimeException {
    private final int statusCode;

    public BadClientRequestException(String message, int statusCode){
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

