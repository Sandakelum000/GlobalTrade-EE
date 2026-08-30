package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.AdminOrderServiceException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AdminOrderServiceExceptionMapper implements ExceptionMapper<AdminOrderServiceException> {
    @Override
    public Response toResponse(AdminOrderServiceException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Admin Order details get failed",
                "AdminOrder service error",
                exception.getMessage(),
                statusCode
        );
        return  Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
    }
}
