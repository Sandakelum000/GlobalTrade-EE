package com.globaltrade.logistics.core.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) {
        super("Product already exists: " + message);
    }
}
