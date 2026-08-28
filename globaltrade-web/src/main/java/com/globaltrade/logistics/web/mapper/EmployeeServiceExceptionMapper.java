package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.EmployeeServiceException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EmployeeServiceExceptionMapper implements ExceptionMapper<EmployeeServiceException> {
    @Override
    public Response toResponse(EmployeeServiceException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();

        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Employee Service",
                "Employee",
                exception.getMessage(),
                statusCode
        );
        return Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
    }
}
