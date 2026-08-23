package com.globaltrade.logistics.web.dto.error;

public record ValidationErrorResponse(
        String message,
        String field,
        String detail,
        int status
) {
    public static ValidationErrorResponse of(String message, String field, String detail, int status) {
        return new ValidationErrorResponse(message, field, detail, status);
    }
}
