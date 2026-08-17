package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ConstraintViolation<?> violation = exception.getConstraintViolations()
                .stream()
                .findFirst()
                .orElse(null);

        String field = "unknown";
        String message = "validation failed";

        if(violation != null) {
            field = extractFileName(violation);
            message = violation.getMessage();
        }

        int status = Response.Status.BAD_REQUEST.getStatusCode();
        ValidationErrorResponse response =
                ValidationErrorResponse.of("Validation failed",field, message, status);

        return Response.status(status).type(MediaType.APPLICATION_JSON).entity(response).build();
    }

    private String extractFileName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        if(path.contains(".")) {
            return path.substring(path.lastIndexOf('.') + 1);
        }
        return path;
    }
}
