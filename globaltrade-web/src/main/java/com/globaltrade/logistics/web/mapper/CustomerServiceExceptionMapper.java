package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.CustomerServiceException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerServiceExceptionMapper implements ExceptionMapper<CustomerServiceException> {
    @Override
    public Response toResponse(CustomerServiceException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Customer Service",
                "Admin customer service",
                exception.getMessage(),
                statusCode
        );
        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse).build();
    }
}
