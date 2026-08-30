package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ShipmentShippingException extends RuntimeException {
    public ShipmentShippingException(String message) {
        super(message);
    }
}
