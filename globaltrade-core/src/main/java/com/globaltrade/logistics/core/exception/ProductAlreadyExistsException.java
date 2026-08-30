package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) {
        super("Product already exists: " + message);
    }
}
