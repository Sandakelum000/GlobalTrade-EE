package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.AuditServiceException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuditServiceExceptionMapper implements ExceptionMapper<AuditServiceException> {
    @Override
    public Response toResponse(AuditServiceException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Audit log failed",
                "Audit service",
                exception.getMessage(),
                statusCode
        );
        return  Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
    }
}
