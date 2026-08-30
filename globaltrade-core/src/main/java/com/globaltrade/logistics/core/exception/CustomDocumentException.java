package com.globaltrade.logistics.core.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException
public class CustomDocumentException extends RuntimeException {
    public CustomDocumentException(String message) {
        super(message);
    }
}
