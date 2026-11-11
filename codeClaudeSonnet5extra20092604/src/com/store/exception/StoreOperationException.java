package com.store.exception;

public class StoreOperationException extends RuntimeException {
    public StoreOperationException(String message) {
        super(message);
    }
}