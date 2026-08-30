package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class GRNCreationException extends RuntimeException {
    public GRNCreationException(String message) {
        super(message);
    }
}
