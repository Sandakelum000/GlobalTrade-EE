package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException
public class AdminShipmentServiceException extends RuntimeException {
    public AdminShipmentServiceException(String message) {
        super(message);
    }
}
