package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException()
public class VendorServiceException extends RuntimeException {
    public VendorServiceException(String message) {
        super(message);
    }
}
