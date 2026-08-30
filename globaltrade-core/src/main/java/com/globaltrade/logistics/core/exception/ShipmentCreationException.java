package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ShipmentCreationException extends RuntimeException {
    public ShipmentCreationException(String message) {
        super(message);
    }
}
