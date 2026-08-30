package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class AuditServiceException extends RuntimeException {
    public AuditServiceException(String message) {
        super(message);
    }
}
