package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class OrderCancellationException extends RuntimeException {
    public OrderCancellationException(String message) {
        super(message);
    }
}
