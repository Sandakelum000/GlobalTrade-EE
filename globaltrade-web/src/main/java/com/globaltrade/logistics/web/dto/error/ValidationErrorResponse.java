package com.globaltrade.logistics.web.dto.error;

public record ValidationErrorResponse(
        String error,
        String field,
        String message,
        int status,
        long timestamp
) {
    public static ValidationErrorResponse of(String error, String field, String message, int status) {
        return new ValidationErrorResponse(error, field, message, status, System.currentTimeMillis());
    }
}
