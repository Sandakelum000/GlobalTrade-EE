package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {

        List<ValidationErrorResponse> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String field = extractFileName(violation);
                    return ValidationErrorResponse.of(
                            "Validation failed",
                            field,
                            violation.getMessage(),
                            Response.Status.BAD_REQUEST.getStatusCode()
                    );

                }).toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(errors)
                .build();
    }

    private String extractFileName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        if(path.contains(".")) {
            return path.substring(path.lastIndexOf('.') + 1);
        }
        return path;
    }
}
