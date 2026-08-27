package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.VendorServiceException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class VendorServiceExceptionMapper implements ExceptionMapper<VendorServiceException> {
    @Override
    public Response toResponse(VendorServiceException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Vendor service failed",
                "Vendor",
                exception.getMessage(),
                statusCode
        );
        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse).build();
    }
}
