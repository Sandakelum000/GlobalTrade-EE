package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CheckoutException extends RuntimeException {
    public CheckoutException(String message) {
        super(message);
    }
}
