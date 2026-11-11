package com.store.exception;

public class ReturnQuantityExceededException extends RuntimeException {
    public ReturnQuantityExceededException(String message) {
        super(message);
    }
}