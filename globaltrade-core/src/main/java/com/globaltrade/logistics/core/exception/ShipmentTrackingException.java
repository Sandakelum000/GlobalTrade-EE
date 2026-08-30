package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ShipmentTrackingException extends RuntimeException {
    public ShipmentTrackingException(String message) {
        super(message);
    }
}
