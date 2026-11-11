package com.store.exception;

public class EmptyReturnException extends RuntimeException {
    public EmptyReturnException(String message) {
        super(message);
    }
}