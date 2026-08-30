package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException
public class AdminOrderServiceException extends RuntimeException {
    public AdminOrderServiceException(String message) {
        super(message);
    }
}
