package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
