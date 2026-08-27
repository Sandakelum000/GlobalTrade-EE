package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.GRNCreationException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GRNCreationExceptionMapper implements ExceptionMapper<GRNCreationException> {
    @Override
    public Response toResponse(GRNCreationException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Grn creation failed",
                "Create GRN",
                exception.getMessage(),
                statusCode
        );
        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse).build();
    }
}
