package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException
public class AdminInventoryServiceException extends RuntimeException {
    public AdminInventoryServiceException(String message) {
        super(message);
    }
}
